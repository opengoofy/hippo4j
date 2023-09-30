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

package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.server.common.base.Results;
import cn.hippo4j.config.model.biz.item.ItemQueryReqDTO;
import cn.hippo4j.config.model.biz.item.ItemRespDTO;
import cn.hippo4j.config.model.biz.item.ItemSaveReqDTO;
import cn.hippo4j.config.model.biz.item.ItemUpdateReqDTO;
import cn.hippo4j.config.service.biz.ItemService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Item controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/item")
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/query/page")
    public Result<IPage<ItemRespDTO>> queryItemPage(@RequestBody ItemQueryReqDTO reqDTO) {
        return Results.success(itemService.queryItemPage(reqDTO));
    }

    @GetMapping("/query/{tenantId}/{itemId}")
    public Result queryItemById(@PathVariable("tenantId") String tenantId, @PathVariable("itemId") String itemId) {
        return Results.success(itemService.queryItemById(tenantId, itemId));
    }

    @PostMapping("/save")
    public Result saveItem(@Validated @RequestBody ItemSaveReqDTO reqDTO) {
        itemService.saveItem(reqDTO);
        return Results.success();
    }

    @PostMapping("/update")
    public Result updateItem(@RequestBody ItemUpdateReqDTO reqDTO) {
        itemService.updateItem(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/delete/{tenantId}/{itemId}")
    public Result deleteItem(@PathVariable("tenantId") String tenantId, @PathVariable("itemId") String itemId) {
        itemService.deleteItem(tenantId, itemId);
        return Results.success();
    }
}
