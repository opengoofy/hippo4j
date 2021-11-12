package com.github.dynamic.threadpool.console.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.model.InstanceInfo;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import com.github.dynamic.threadpool.config.model.CacheItem;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolDelReqDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolRespDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import com.github.dynamic.threadpool.config.service.ConfigCacheService;
import com.github.dynamic.threadpool.config.service.biz.ThreadPoolService;
import com.github.dynamic.threadpool.config.toolkit.BeanUtil;
import com.github.dynamic.threadpool.console.model.ThreadPoolInstanceInfo;
import com.github.dynamic.threadpool.discovery.core.BaseInstanceRegistry;
import com.github.dynamic.threadpool.discovery.core.Lease;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.github.dynamic.threadpool.common.toolkit.ContentUtil.getGroupKey;

/**
 * Thread pool controller.
 *
 * @author chen.ma
 * @date 2021/6/30 20:54
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/thread/pool")
public class ThreadPoolController {

    private final ThreadPoolService threadPoolService;

    private final BaseInstanceRegistry baseInstanceRegistry;

    @PostMapping("/query/page")
    public Result<IPage<ThreadPoolRespDTO>> queryNameSpacePage(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(threadPoolService.queryThreadPoolPage(reqDTO));
    }

    @PostMapping("/query")
    public Result<ThreadPoolRespDTO> queryNameSpace(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(threadPoolService.getThreadPool(reqDTO));
    }

    @PostMapping("/save_or_update")
    public Result saveOrUpdateThreadPoolConfig(@RequestParam(value = "identify", required = false) String identify,
                                               @RequestBody ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        threadPoolService.saveOrUpdateThreadPoolConfig(identify, reqDTO);
        return Results.success();
    }

    @GetMapping("/list/instance/{itemId}/{tpId}")
    public Result<List<ThreadPoolInstanceInfo>> listInstance(@PathVariable("itemId") String itemId, @PathVariable("tpId") String tpId) {
        List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(itemId);
        Lease<InstanceInfo> first = CollUtil.getFirst(leases);
        if (first == null) {
            return Results.success(Lists.newArrayList());
        }

        InstanceInfo holder = first.getHolder();
        String itemTenantKey = holder.getGroupKey();
        String groupKey = getGroupKey(tpId, itemTenantKey);
        Map<String, CacheItem> content = ConfigCacheService.getContent(groupKey);

        List<ThreadPoolInstanceInfo> returnThreadPool = Lists.newArrayList();
        content.forEach((key, val) -> {
            ThreadPoolInstanceInfo threadPoolInstanceInfo = BeanUtil.convert(val.configAllInfo, ThreadPoolInstanceInfo.class);
            threadPoolInstanceInfo.setIdentify(key);
            threadPoolInstanceInfo.setClientBasePath(holder.getClientBasePath());
            returnThreadPool.add(threadPoolInstanceInfo);
        });

        return Results.success(returnThreadPool);
    }

    @DeleteMapping("/delete")
    public Result deletePool(@RequestBody ThreadPoolDelReqDTO reqDTO) {
        threadPoolService.deletePool(reqDTO);
        return Results.success();
    }

}
