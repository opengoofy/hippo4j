package io.dynamic.threadpool.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.web.base.Result;
import io.dynamic.threadpool.common.web.base.Results;
import io.dynamic.threadpool.server.model.biz.item.ItemQueryReqDTO;
import io.dynamic.threadpool.server.model.biz.item.ItemRespDTO;
import io.dynamic.threadpool.server.model.biz.item.ItemSaveReqDTO;
import io.dynamic.threadpool.server.service.biz.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Item Controller.
 *
 * @author chen.ma
 * @date 2021/6/29 21:42
 */
@RestController
@RequestMapping(Constants.BASE_PATH)
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/item/query/page")
    public Result<IPage<ItemRespDTO>> queryItemPage(@RequestBody ItemQueryReqDTO reqDTO) {
        return Results.success(itemService.queryItemPage(reqDTO));
    }

    @GetMapping("/item/query/{itemId}")
    public Result queryItemById(@PathVariable("itemId") String itemId) {
        return Results.success(itemService.queryItemById(itemId));
    }

    @PostMapping("/item/save")
    public Result saveItem(@RequestBody ItemSaveReqDTO reqDTO) {
        itemService.saveItem(reqDTO);
        return Results.success();
    }

    @PostMapping("/item/update")
    public Result updateItem() {

        return Results.success();
    }

    @DeleteMapping("/item/delete/{itemId}")
    public Result deleteItem(@PathVariable("itemId") String itemId) {

        return Results.success();
    }
}
