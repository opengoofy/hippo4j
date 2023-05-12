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

package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.item.ItemRespDTO;
import cn.hippo4j.config.model.biz.item.ItemUpdateReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.config.model.biz.item.ItemQueryReqDTO;
import cn.hippo4j.config.model.biz.item.ItemSaveReqDTO;

import java.util.List;

/**
 * Item service.
 */
public interface ItemService {

    /**
     * Query item page.
     *
     * @param reqDTO
     * @return
     */
    IPage<ItemRespDTO> queryItemPage(ItemQueryReqDTO reqDTO);

    /**
     * Query item by id.
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    ItemRespDTO queryItemById(String tenantId, String itemId);

    /**
     * Query item.
     *
     * @param reqDTO
     * @return
     */
    List<ItemRespDTO> queryItem(ItemQueryReqDTO reqDTO);

    /**
     * Save item.
     *
     * @param reqDTO
     */
    void saveItem(ItemSaveReqDTO reqDTO);

    /**
     * Update item.
     *
     * @param reqDTO
     */
    void updateItem(ItemUpdateReqDTO reqDTO);

    /**
     * Delete item.
     *
     * @param tenantId
     * @param itemId
     */
    void deleteItem(String tenantId, String itemId);
}
