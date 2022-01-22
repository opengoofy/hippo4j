package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.CacheItem;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolDelReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.service.biz.ThreadPoolService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hippo4j.console.model.ThreadPoolInstanceInfo;
import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hippo4j.common.toolkit.ContentUtil.getGroupKey;

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
                                               @Validated @RequestBody ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        threadPoolService.saveOrUpdateThreadPoolConfig(identify, reqDTO);
        return Results.success();
    }

    @DeleteMapping("/delete")
    public Result deletePool(@RequestBody ThreadPoolDelReqDTO reqDTO) {
        threadPoolService.deletePool(reqDTO);
        return Results.success();
    }

    @PostMapping("/alarm/enable/{id}/{isAlarm}")
    public Result alarmEnable(@PathVariable("id") String id, @PathVariable("isAlarm") Integer isAlarm) {
        threadPoolService.alarmEnable(id, isAlarm);
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
        Map<String, String> activeMap = leases.stream()
                .map(each -> each.getHolder())
                .filter(each -> StringUtil.isNotBlank(each.getActive()))
                .collect(Collectors.toMap(InstanceInfo::getIdentify, InstanceInfo::getActive));

        Map<String, String> clientBasePathMap = leases.stream()
                .map(each -> each.getHolder())
                .filter(each -> StringUtil.isNotBlank(each.getClientBasePath()))
                .collect(Collectors.toMap(InstanceInfo::getIdentify, InstanceInfo::getClientBasePath));

        List<ThreadPoolInstanceInfo> returnThreadPool = Lists.newArrayList();
        content.forEach((key, val) -> {
            ThreadPoolInstanceInfo threadPoolInstanceInfo = BeanUtil.convert(val.configAllInfo, ThreadPoolInstanceInfo.class);
            threadPoolInstanceInfo.setClientAddress(StrUtil.subBefore(key, Constants.IDENTIFY_SLICER_SYMBOL, false));
            threadPoolInstanceInfo.setActive(activeMap.get(key));
            threadPoolInstanceInfo.setIdentify(key);
            threadPoolInstanceInfo.setClientBasePath(clientBasePathMap.get(key));
            returnThreadPool.add(threadPoolInstanceInfo);
        });

        return Results.success(returnThreadPool);
    }

    @GetMapping("/list/client/instance/{itemId}")
    public Result listClientInstance(@PathVariable("itemId") String itemId) {
        List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(itemId);
        Lease<InstanceInfo> first = CollUtil.getFirst(leases);
        if (first == null) {
            return Results.success(Lists.newArrayList());
        }

        List<ThreadPoolInstanceInfo> returnThreadPool = Lists.newArrayList();
        leases.forEach(each -> {
            InstanceInfo holder = each.getHolder();
            ThreadPoolInstanceInfo threadPoolInstanceInfo = new ThreadPoolInstanceInfo();
            threadPoolInstanceInfo.setActive(holder.getActive());
            threadPoolInstanceInfo.setClientAddress(holder.getCallBackUrl());
            threadPoolInstanceInfo.setIdentify(holder.getIdentify());

            returnThreadPool.add(threadPoolInstanceInfo);
        });

        return Results.success(returnThreadPool);
    }

}
