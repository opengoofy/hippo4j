package io.dynamic.threadpool.server.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.dynamic.threadpool.server.enums.DelEnum;
import io.dynamic.threadpool.server.mapper.TenantInfoMapper;
import io.dynamic.threadpool.server.model.TenantInfo;
import io.dynamic.threadpool.server.model.biz.item.ItemQueryReqDTO;
import io.dynamic.threadpool.server.model.biz.item.ItemRespDTO;
import io.dynamic.threadpool.server.model.biz.tenant.TenantQueryReqDTO;
import io.dynamic.threadpool.server.model.biz.tenant.TenantRespDTO;
import io.dynamic.threadpool.server.model.biz.tenant.TenantSaveReqDTO;
import io.dynamic.threadpool.server.model.biz.tenant.TenantUpdateReqDTO;
import io.dynamic.threadpool.server.toolkit.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务接口
 *
 * @author chen.ma
 * @date 2021/6/29 21:12
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private ItemService itemService;

    @Resource
    private TenantInfoMapper tenantInfoMapper;

    @Override
    public TenantRespDTO getNameSpaceById(String namespaceId) {
        LambdaQueryWrapper<TenantInfo> queryWrapper = Wrappers
                .lambdaQuery(TenantInfo.class).eq(TenantInfo::getTenantId, namespaceId);
        TenantInfo tenantInfo = tenantInfoMapper.selectOne(queryWrapper);

        TenantRespDTO result = BeanUtil.convert(tenantInfo, TenantRespDTO.class);
        return result;
    }

    @Override
    public IPage<TenantRespDTO> queryNameSpacePage(TenantQueryReqDTO reqDTO) {
        LambdaQueryWrapper<TenantInfo> wrapper = Wrappers.lambdaQuery(TenantInfo.class)
                .eq(!StringUtils.isEmpty(reqDTO.getTenantId()), TenantInfo::getTenantId, reqDTO.getTenantId())
                .eq(!StringUtils.isEmpty(reqDTO.getTenantName()), TenantInfo::getTenantName, reqDTO.getTenantName())
                .eq(!StringUtils.isEmpty(reqDTO.getOwner()), TenantInfo::getOwner, reqDTO.getOwner());
        Page resultPage = tenantInfoMapper.selectPage(reqDTO, wrapper);

        return resultPage.convert(each -> BeanUtil.convert(each, TenantRespDTO.class));
    }

    @Override
    public void saveNameSpace(TenantSaveReqDTO reqDTO) {
        TenantInfo tenantInfo = BeanUtil.convert(reqDTO, TenantInfo.class);
        int insertResult = tenantInfoMapper.insert(tenantInfo);

        boolean retBool = SqlHelper.retBool(insertResult);
        if (!retBool) {
            throw new RuntimeException("插入失败.");
        }
    }

    @Override
    public void updateNameSpace(TenantUpdateReqDTO reqDTO) {
        TenantInfo tenantInfo = BeanUtil.convert(reqDTO, TenantInfo.class);
        int updateResult = tenantInfoMapper.update(tenantInfo, Wrappers
                .lambdaUpdate(TenantInfo.class).eq(TenantInfo::getTenantId, reqDTO.getNamespaceId()));
        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("修改失败.");
        }
    }

    @Override
    public void deleteNameSpaceById(String namespaceId) {
        ItemQueryReqDTO reqDTO = new ItemQueryReqDTO();
        reqDTO.setTenantId(namespaceId);
        List<ItemRespDTO> itemList = itemService.queryItem(reqDTO);
        if (CollectionUtils.isNotEmpty(itemList)) {
            throw new RuntimeException("业务线包含项目引用, 删除失败.");
        }

        int updateResult = tenantInfoMapper.update(new TenantInfo(),
                Wrappers.lambdaUpdate(TenantInfo.class)
                        .eq(TenantInfo::getTenantId, namespaceId)
                        .set(TenantInfo::getDelFlag, DelEnum.DELETE.getIntCode()));
        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("删除失败.");
        }
    }
}
