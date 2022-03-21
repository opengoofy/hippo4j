package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.enums.DelEnum;
import cn.hippo4j.config.mapper.ItemInfoMapper;
import cn.hippo4j.config.model.ItemInfo;
import cn.hippo4j.config.model.biz.item.ItemQueryReqDTO;
import cn.hippo4j.config.model.biz.item.ItemRespDTO;
import cn.hippo4j.config.model.biz.item.ItemSaveReqDTO;
import cn.hippo4j.config.model.biz.item.ItemUpdateReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.service.biz.ItemService;
import cn.hippo4j.config.service.biz.ThreadPoolService;
import cn.hippo4j.config.toolkit.BeanUtil;
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
 * Item service impl.
 *
 * @author chen.ma
 * @date 2021/6/29 21:58
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemInfoMapper itemInfoMapper;

    private final ThreadPoolService threadPoolService;

    @Override
    public IPage<ItemRespDTO> queryItemPage(ItemQueryReqDTO reqDTO) {
        LambdaQueryWrapper<ItemInfo> wrapper = Wrappers.lambdaQuery(ItemInfo.class)
                .eq(!StringUtils.isEmpty(reqDTO.getItemId()), ItemInfo::getItemId, reqDTO.getItemId())
                .eq(!StringUtils.isEmpty(reqDTO.getItemName()), ItemInfo::getItemName, reqDTO.getItemName())
                .eq(!StringUtils.isEmpty(reqDTO.getTenantId()), ItemInfo::getTenantId, reqDTO.getTenantId())
                .eq(!StringUtils.isEmpty(reqDTO.getOwner()), ItemInfo::getOwner, reqDTO.getOwner());

        Page<ItemInfo> resultPage = itemInfoMapper.selectPage((Page)reqDTO, wrapper);
        return resultPage.convert(each -> BeanUtil.convert(each, ItemRespDTO.class));
    }

    @Override
    public ItemRespDTO queryItemById(String tenantId, String itemId) {
        LambdaQueryWrapper<ItemInfo> queryWrapper = Wrappers
                .lambdaQuery(ItemInfo.class)
                .eq(ItemInfo::getTenantId, tenantId)
                .eq(ItemInfo::getItemId, itemId);

        ItemInfo itemInfo = itemInfoMapper.selectOne(queryWrapper);
        ItemRespDTO result = BeanUtil.convert(itemInfo, ItemRespDTO.class);
        return result;
    }

    @Override
    public List<ItemRespDTO> queryItem(ItemQueryReqDTO reqDTO) {
        LambdaQueryWrapper<ItemInfo> wrapper = Wrappers.lambdaQuery(ItemInfo.class)
                .eq(!StringUtils.isEmpty(reqDTO.getItemId()), ItemInfo::getItemId, reqDTO.getItemId())
                .eq(!StringUtils.isEmpty(reqDTO.getTenantId()), ItemInfo::getTenantId, reqDTO.getTenantId());

        List<ItemInfo> itemInfos = itemInfoMapper.selectList(wrapper);
        return BeanUtil.convert(itemInfos, ItemRespDTO.class);
    }

    @Override
    public void saveItem(ItemSaveReqDTO reqDTO) {
        LambdaQueryWrapper<ItemInfo> queryWrapper = Wrappers.lambdaQuery(ItemInfo.class)
                .eq(ItemInfo::getItemId, reqDTO.getItemId());

        // 当前为单体应用, 后续支持集群部署时切换分布式锁.
        synchronized (ItemService.class) {
            ItemInfo existItemInfo = itemInfoMapper.selectOne(queryWrapper);
            Assert.isNull(existItemInfo, "项目配置已存在.");

            ItemInfo itemInfo = BeanUtil.convert(reqDTO, ItemInfo.class);
            int insertResult = itemInfoMapper.insert(itemInfo);

            boolean retBool = SqlHelper.retBool(insertResult);
            if (!retBool) {
                throw new RuntimeException("Save error");
            }
        }
    }

    @Override
    public void updateItem(ItemUpdateReqDTO reqDTO) {
        ItemInfo itemInfo = BeanUtil.convert(reqDTO, ItemInfo.class);
        int updateResult = itemInfoMapper.update(itemInfo,
                Wrappers.lambdaUpdate(ItemInfo.class)
                        .eq(ItemInfo::getTenantId, reqDTO.getTenantId())
                        .eq(ItemInfo::getItemId, reqDTO.getItemId()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Update error.");
        }
    }

    @Override
    public void deleteItem(String namespace, String itemId) {
        List<ThreadPoolRespDTO> itemList = threadPoolService.getThreadPoolByItemId(itemId);
        if (CollectionUtils.isNotEmpty(itemList)) {
            throw new RuntimeException("项目包含线程池引用, 删除失败.");
        }

        int updateResult = itemInfoMapper.update(new ItemInfo(),
                Wrappers.lambdaUpdate(ItemInfo.class)
                        .eq(ItemInfo::getTenantId, namespace)
                        .eq(ItemInfo::getItemId, itemId)
                        .set(ItemInfo::getDelFlag, DelEnum.DELETE.getIntCode()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Delete error.");
        }
    }

}
