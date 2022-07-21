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
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
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
import cn.hippo4j.console.model.WebThreadPoolReqDTO;
import cn.hippo4j.console.model.WebThreadPoolRespDTO;
import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hippo4j.common.constant.Constants.HTTP_EXECUTE_TIMEOUT;
import static cn.hippo4j.common.toolkit.ContentUtil.getGroupKey;

/**
 * Thread pool controller.
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

    @GetMapping("/run/state/{tpId}")
    public Result runState(@PathVariable("tpId") String tpId,
                           @RequestParam(value = "clientAddress") String clientAddress) {
        String urlString = StrBuilder.create("http://", clientAddress, "/run/state/", tpId).toString();
        String data = HttpUtil.get(urlString, HTTP_EXECUTE_TIMEOUT);
        Result result = JSONUtil.parseObject(data, Result.class);
        return result;
    }

    @GetMapping("/run/thread/state/{tpId}")
    public Result runThreadState(@PathVariable("tpId") String tpId,
                                 @RequestParam(value = "clientAddress") String clientAddress) {
        String urlString = StrBuilder.create("http://", clientAddress, "/run/thread/state/", tpId).toString();
        String data = HttpUtil.get(urlString, HTTP_EXECUTE_TIMEOUT);
        Result result = JSONUtil.parseObject(data, Result.class);
        return result;
    }

    @GetMapping("/list/client/instance/{itemId}")
    public Result listClientInstance(@PathVariable("itemId") String itemId) {
        List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(itemId);
        Lease<InstanceInfo> first = CollUtil.getFirst(leases);
        if (first == null) {
            return Results.success(Lists.newArrayList());
        }
        List<WebThreadPoolRespDTO> returnThreadPool = Lists.newArrayList();
        for (Lease<InstanceInfo> each : leases) {
            Result poolBaseState;
            try {
                poolBaseState = getPoolBaseState(each.getHolder().getCallBackUrl());
            } catch (Throwable ignored) {
                continue;
            }
            Object data = poolBaseState.getData();
            if (data == null) {
                continue;
            }
            WebThreadPoolRespDTO result = BeanUtil.convert(data, WebThreadPoolRespDTO.class);
            result.setActive(each.getHolder().getActive());
            result.setIdentify(each.getHolder().getIdentify());
            result.setClientAddress(each.getHolder().getCallBackUrl());
            returnThreadPool.add(result);
        }
        return Results.success(returnThreadPool);
    }

    @GetMapping("/web/base/info")
    public Result getPoolBaseState(@RequestParam(value = "clientAddress") String clientAddress) {
        String urlString = StrBuilder.create("http://", clientAddress, "/web/base/info").toString();
        String data = HttpUtil.get(urlString, HTTP_EXECUTE_TIMEOUT);
        Result result = JSONUtil.parseObject(data, Result.class);
        return result;
    }

    @GetMapping("/web/run/state")
    public Result getPoolRunState(@RequestParam(value = "clientAddress") String clientAddress) {
        String urlString = StrBuilder.create("http://", clientAddress, "/web/run/state").toString();
        String data = HttpUtil.get(urlString, HTTP_EXECUTE_TIMEOUT);
        Result result = JSONUtil.parseObject(data, Result.class);
        return result;
    }

    @PostMapping("/web/update/pool")
    public Result<Void> updateWebThreadPool(@RequestBody WebThreadPoolReqDTO requestParam) {
        for (String each : requestParam.getClientAddressList()) {
            String urlString = StrBuilder.create("http://", each, "/web/update/pool").toString();
            HttpUtil.post(urlString, JSONUtil.toJSONString(requestParam), HTTP_EXECUTE_TIMEOUT);
        }
        return Results.success();
    }

    @GetMapping("/list/instance/{itemId}/{tpId}")
    public Result<List<ThreadPoolInstanceInfo>> listInstance(@PathVariable("itemId") String itemId,
                                                             @PathVariable("tpId") String tpId) {
        List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(itemId);
        Lease<InstanceInfo> first = CollUtil.getFirst(leases);
        if (first == null) {
            return Results.success(Lists.newArrayList());
        }
        InstanceInfo holder = first.getHolder();
        String itemTenantKey = holder.getGroupKey();
        String groupKey = getGroupKey(tpId, itemTenantKey);
        Map<String, CacheItem> content = ConfigCacheService.getContent(groupKey);
        Map<String, String> activeMap =
                leases.stream().map(each -> each.getHolder()).filter(each -> StringUtil.isNotBlank(each.getActive()))
                        .collect(Collectors.toMap(InstanceInfo::getIdentify, InstanceInfo::getActive));
        Map<String, String> clientBasePathMap = leases.stream().map(each -> each.getHolder())
                .filter(each -> StringUtil.isNotBlank(each.getClientBasePath()))
                .collect(Collectors.toMap(InstanceInfo::getIdentify, InstanceInfo::getClientBasePath));
        List<ThreadPoolInstanceInfo> returnThreadPool = Lists.newArrayList();
        content.forEach((key, val) -> {
            ThreadPoolInstanceInfo threadPoolInstanceInfo =
                    BeanUtil.convert(val.configAllInfo, ThreadPoolInstanceInfo.class);
            threadPoolInstanceInfo.setClientAddress(StrUtil.subBefore(key, Constants.IDENTIFY_SLICER_SYMBOL, false));
            threadPoolInstanceInfo.setActive(activeMap.get(key));
            threadPoolInstanceInfo.setIdentify(key);
            threadPoolInstanceInfo.setClientBasePath(clientBasePathMap.get(key));
            returnThreadPool.add(threadPoolInstanceInfo);
        });
        return Results.success(returnThreadPool);
    }
}
