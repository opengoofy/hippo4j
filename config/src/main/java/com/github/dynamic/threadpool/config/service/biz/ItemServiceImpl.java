package com.github.dynamic.threadpool.config.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.dynamic.threadpool.config.enums.DelEnum;
import com.github.dynamic.threadpool.config.mapper.ItemInfoMapper;
import com.github.dynamic.threadpool.config.model.ItemInfo;
import com.github.dynamic.threadpool.config.model.biz.item.ItemQueryReqDTO;
import com.github.dynamic.threadpool.config.model.biz.item.ItemRespDTO;
import com.github.dynamic.threadpool.config.model.biz.item.ItemSaveReqDTO;
import com.github.dynamic.threadpool.config.model.biz.item.ItemUpdateReqDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolRespDTO;
import com.github.dynamic.threadpool.config.toolkit.BeanUtil;
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
        Page<ItemInfo> resultPage = itemInfoMapper.selectPage(reqDTO, wrapper);

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
        ItemInfo itemInfo = BeanUtil.convert(reqDTO, ItemInfo.class);
        int insertResult = itemInfoMapper.insert(itemInfo);

        boolean retBool = SqlHelper.retBool(insertResult);
        if (!retBool) {
            throw new RuntimeException("Save error");
        }
    }

    @Override
    public void updateItem(ItemUpdateReqDTO reqDTO) {
        ItemInfo itemInfo = BeanUtil.convert(reqDTO, ItemInfo.class);
        int updateResult = itemInfoMapper.update(itemInfo, Wrappers
                .lambdaUpdate(ItemInfo.class)
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
            throw new RuntimeException("The project contains a thread pool reference, and the deletion failed.");
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
