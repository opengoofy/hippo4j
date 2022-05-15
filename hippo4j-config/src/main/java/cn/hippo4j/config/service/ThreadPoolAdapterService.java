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

package cn.hippo4j.config.service;

import cn.hippo4j.adapter.base.ThreadPoolAdapterCacheConfig;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.config.model.biz.adapter.ThreadPoolAdapterReqDTO;
import cn.hippo4j.config.model.biz.adapter.ThreadPoolAdapterRespDTO;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.http.HttpUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.hippo4j.common.constant.Constants.HTTP_EXECUTE_TIMEOUT;
import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;

/**
 * Thread-pool adapter service.
 */
@Slf4j
@Service
public class ThreadPoolAdapterService {

    /**
     * Map<mark, Map<tenantItem, Map<threadPoolKey, List<String>>>>
     */
    private final Map<String, Map<String, Map<String, List<String>>>> THREAD_POOL_ADAPTER_MAP = Maps.newConcurrentMap();

    public synchronized void register(List<ThreadPoolAdapterCacheConfig> requestParameter) {
        for (ThreadPoolAdapterCacheConfig each : requestParameter) {
            String mark = each.getMark();
            Map<String, Map<String, List<String>>> actual = THREAD_POOL_ADAPTER_MAP.get(mark);
            if (CollectionUtil.isEmpty(actual)) {
                actual = Maps.newHashMap();
                THREAD_POOL_ADAPTER_MAP.put(mark, actual);
            }
            Map<String, List<String>> tenantItemMap = actual.get(each.getTenantItemKey());
            if (CollectionUtil.isEmpty(tenantItemMap)) {
                tenantItemMap = Maps.newHashMap();
                actual.put(each.getTenantItemKey(), tenantItemMap);
            }
            List<ThreadPoolAdapterState> threadPoolAdapterStates = each.getThreadPoolAdapterStates();
            for (ThreadPoolAdapterState adapterState : threadPoolAdapterStates) {
                List<String> threadPoolKeyList = tenantItemMap.get(adapterState.getThreadPoolKey());
                if (CollectionUtil.isEmpty(threadPoolKeyList)) {
                    threadPoolKeyList = Lists.newArrayList();
                    tenantItemMap.put(adapterState.getThreadPoolKey(), threadPoolKeyList);
                }
                threadPoolKeyList.add(each.getClientAddress());
            }
        }
    }

    public List<ThreadPoolAdapterRespDTO> query(ThreadPoolAdapterReqDTO requestParameter) {
        List<String> actual = Optional.ofNullable(THREAD_POOL_ADAPTER_MAP.get(requestParameter.getMark()))
                .map(each -> each.get(requestParameter.getTenant() + IDENTIFY_SLICER_SYMBOL + requestParameter.getItem()))
                .map(each -> each.get(requestParameter.getThreadPoolKey()))
                .orElse(Lists.newArrayList());
        List<ThreadPoolAdapterRespDTO> result = Lists.newCopyOnWriteArrayList();
        actual.parallelStream().forEach(each -> {
            String urlString = StrBuilder.create("http://", each, "/adapter/thread-pool/info").toString();
            Map<String, Object> param = Maps.newHashMap();
            param.put("mark", requestParameter.getMark());
            param.put("threadPoolKey", requestParameter.getThreadPoolKey());
            try {
                String resultStr = HttpUtil.get(urlString, param, HTTP_EXECUTE_TIMEOUT);
                if (StringUtil.isNotBlank(resultStr)) {
                    Result<ThreadPoolAdapterRespDTO> restResult = JSONUtil.parseObject(resultStr, Result.class);
                    result.add(restResult.getData());
                }
            } catch (Throwable ex) {
                log.error("Failed to get third-party thread pool data.", ex);
            }
        });
        return result;
    }
}
