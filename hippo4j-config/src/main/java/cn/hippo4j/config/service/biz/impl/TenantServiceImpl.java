package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.enums.DelEnum;
import cn.hippo4j.config.mapper.TenantInfoMapper;
import cn.hippo4j.config.model.TenantInfo;
import cn.hippo4j.config.model.biz.item.ItemQueryReqDTO;
import cn.hippo4j.config.model.biz.item.ItemRespDTO;
import cn.hippo4j.config.model.biz.tenant.TenantQueryReqDTO;
import cn.hippo4j.config.model.biz.tenant.TenantRespDTO;
import cn.hippo4j.config.model.biz.tenant.TenantSaveReqDTO;
import cn.hippo4j.config.model.biz.tenant.TenantUpdateReqDTO;
import cn.hippo4j.config.service.biz.ItemService;
import cn.hippo4j.config.service.biz.TenantService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hippo4j.tools.logrecord.annotation.LogRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Tenant service impl.
 *
 * @author chen.ma
 * @date 2021/6/29 21:12
 */
@Service
@AllArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final ItemService itemService;

    private final TenantInfoMapper tenantInfoMapper;

    @Override
    public TenantRespDTO getTenantById(String id) {
        return BeanUtil.convert(tenantInfoMapper.selectById(id), TenantRespDTO.class);
    }

    @Override
    public TenantRespDTO getTenantByTenantId(String tenantId) {
        LambdaQueryWrapper<TenantInfo> queryWrapper = Wrappers
                .lambdaQuery(TenantInfo.class).eq(TenantInfo::getTenantId, tenantId);

        TenantInfo tenantInfo = tenantInfoMapper.selectOne(queryWrapper);
        TenantRespDTO result = BeanUtil.convert(tenantInfo, TenantRespDTO.class);
        return result;
    }

    @Override
    public IPage<TenantRespDTO> queryTenantPage(TenantQueryReqDTO reqDTO) {
        LambdaQueryWrapper<TenantInfo> wrapper = Wrappers.lambdaQuery(TenantInfo.class)
                .eq(!StringUtils.isEmpty(reqDTO.getTenantId()), TenantInfo::getTenantId, reqDTO.getTenantId())
                .eq(!StringUtils.isEmpty(reqDTO.getTenantName()), TenantInfo::getTenantName, reqDTO.getTenantName())
                .eq(!StringUtils.isEmpty(reqDTO.getOwner()), TenantInfo::getOwner, reqDTO.getOwner());

        Page resultPage = tenantInfoMapper.selectPage((Page)reqDTO, wrapper);
        return resultPage.convert(each -> BeanUtil.convert(each, TenantRespDTO.class));
    }

    @Override
    public void saveTenant(TenantSaveReqDTO reqDTO) {
        LambdaQueryWrapper<TenantInfo> queryWrapper = Wrappers.lambdaQuery(TenantInfo.class)
                .eq(TenantInfo::getTenantId, reqDTO.getTenantId());

        // 当前为单体应用, 后续支持集群部署时切换分布式锁.
        synchronized (TenantService.class) {
            TenantInfo existTenantInfo = tenantInfoMapper.selectOne(queryWrapper);
            Assert.isNull(existTenantInfo, "租户配置已存在.");

            TenantInfo tenantInfo = BeanUtil.convert(reqDTO, TenantInfo.class);
            int insertResult = tenantInfoMapper.insert(tenantInfo);

            boolean retBool = SqlHelper.retBool(insertResult);
            if (!retBool) {
                throw new RuntimeException("Save Error.");
            }
        }
    }

    @Override
    @LogRecord(
            prefix = "item",
            bizNo = "{{#reqDTO.tenantId}}_{{#reqDTO.tenantName}}",
            category = "TENANT_UPDATE",
            success = "更新租户, ID :: {{#reqDTO.id}}, 租户名称由 :: {TENANT{#reqDTO.id}} -> {{#reqDTO.tenantName}}",
            detail = "{{#reqDTO.toString()}}"
    )
    public void updateTenant(TenantUpdateReqDTO reqDTO) {
        TenantInfo tenantInfo = BeanUtil.convert(reqDTO, TenantInfo.class);
        int updateResult = tenantInfoMapper.update(tenantInfo, Wrappers
                .lambdaUpdate(TenantInfo.class).eq(TenantInfo::getTenantId, reqDTO.getTenantId()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Update Error.");
        }
    }

    @Override
    public void deleteTenantById(String tenantId) {
        ItemQueryReqDTO reqDTO = new ItemQueryReqDTO();
        reqDTO.setTenantId(tenantId);
        List<ItemRespDTO> itemList = itemService.queryItem(reqDTO);
        if (CollectionUtils.isNotEmpty(itemList)) {
            throw new RuntimeException("租户包含项目引用, 删除失败.");
        }

        int updateResult = tenantInfoMapper.update(new TenantInfo(),
                Wrappers.lambdaUpdate(TenantInfo.class)
                        .eq(TenantInfo::getTenantId, tenantId)
                        .set(TenantInfo::getDelFlag, DelEnum.DELETE.getIntCode()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Delete error.");
        }
    }
}
