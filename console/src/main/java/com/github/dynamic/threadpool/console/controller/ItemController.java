package com.github.dynamic.threadpool.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import com.github.dynamic.threadpool.config.model.biz.item.ItemQueryReqDTO;
import com.github.dynamic.threadpool.config.model.biz.item.ItemRespDTO;
import com.github.dynamic.threadpool.config.model.biz.item.ItemSaveReqDTO;
import com.github.dynamic.threadpool.config.model.biz.item.ItemUpdateReqDTO;
import com.github.dynamic.threadpool.config.service.biz.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Item controller.
 *
 * @author chen.ma
 * @date 2021/6/29 21:42
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
    public Result saveItem(@RequestBody ItemSaveReqDTO reqDTO) {
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
