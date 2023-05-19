/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.extension.enums.DelEnum;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.mapper.ItemInfoMapper;
import cn.hippo4j.config.model.ItemInfo;
import cn.hippo4j.config.model.biz.item.ItemQueryReqDTO;
import cn.hippo4j.config.model.biz.item.ItemRespDTO;
import cn.hippo4j.config.model.biz.item.ItemSaveReqDTO;
import cn.hippo4j.config.model.biz.item.ItemUpdateReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.service.biz.ItemService;
import cn.hippo4j.config.service.biz.ThreadPoolService;
import cn.hippo4j.common.toolkit.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Item service impl.
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemInfoMapper itemInfoMapper;

    private final ThreadPoolService threadPoolService;

    @Override
    public IPage<ItemRespDTO> queryItemPage(ItemQueryReqDTO reqDTO) {
        LambdaQueryWrapper<ItemInfo> wrapper = Wrappers.lambdaQuery(ItemInfo.class)
                .eq(StringUtil.isNotEmpty(reqDTO.getItemId()), ItemInfo::getItemId, reqDTO.getItemId())
                .eq(StringUtil.isNotEmpty(reqDTO.getItemName()), ItemInfo::getItemName, reqDTO.getItemName())
                .eq(StringUtil.isNotEmpty(reqDTO.getTenantId()), ItemInfo::getTenantId, reqDTO.getTenantId())
                .eq(StringUtil.isNotEmpty(reqDTO.getOwner()), ItemInfo::getOwner, reqDTO.getOwner())
                .orderByDesc(reqDTO.getDesc() != null, ItemInfo::getGmtCreate);
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
                .eq(StringUtil.isNotEmpty(reqDTO.getItemId()), ItemInfo::getItemId, reqDTO.getItemId())
                .eq(StringUtil.isNotEmpty(reqDTO.getTenantId()), ItemInfo::getTenantId, reqDTO.getTenantId());
        List<ItemInfo> itemInfos = itemInfoMapper.selectList(wrapper);
        return BeanUtil.convert(itemInfos, ItemRespDTO.class);
    }

    @Override
    public void saveItem(ItemSaveReqDTO reqDTO) {
        LambdaQueryWrapper<ItemInfo> queryWrapper = Wrappers.lambdaQuery(ItemInfo.class)
                .eq(ItemInfo::getItemId, reqDTO.getItemId());
        // It is currently a single application, and it will support switching distributed locks during cluster deployment in the future.
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
