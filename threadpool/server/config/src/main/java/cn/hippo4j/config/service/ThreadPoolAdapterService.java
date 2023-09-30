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
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.common.extension.design.Observer;
import cn.hippo4j.common.extension.design.ObserverMessage;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.http.HttpUtil;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.config.model.biz.adapter.ThreadPoolAdapterReqDTO;
import cn.hippo4j.config.model.biz.adapter.ThreadPoolAdapterRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;

/**
 * Thread-pool adapter service.
 */
@Slf4j
@Service
public class ThreadPoolAdapterService {

    /**
     * Map&lt;mark, Map&lt;tenantItem, Map&lt;threadPoolKey, List&lt;ThreadPoolAdapterState&gt;&gt;&gt;&gt;
     */
    private static final Map<String, Map<String, Map<String, List<ThreadPoolAdapterState>>>> THREAD_POOL_ADAPTER_MAP = new ConcurrentHashMap<>();

    static {
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.CLEAR_CONFIG_CACHE, new ClearThreadPoolAdapterCache());
    }

    public void register(List<ThreadPoolAdapterCacheConfig> requestParameter) {
        synchronized (ThreadPoolAdapterService.class) {
            for (ThreadPoolAdapterCacheConfig each : requestParameter) {
                String mark = each.getMark();
                Map<String, Map<String, List<ThreadPoolAdapterState>>> actual = THREAD_POOL_ADAPTER_MAP.get(mark);
                if (CollectionUtil.isEmpty(actual)) {
                    actual = new HashMap<>();
                    THREAD_POOL_ADAPTER_MAP.put(mark, actual);
                }
                Map<String, List<ThreadPoolAdapterState>> tenantItemMap = actual.get(each.getTenantItemKey());
                if (CollectionUtil.isEmpty(tenantItemMap)) {
                    tenantItemMap = new HashMap<>();
                    actual.put(each.getTenantItemKey(), tenantItemMap);
                }
                List<ThreadPoolAdapterState> threadPoolAdapterStates = each.getThreadPoolAdapterStates();
                for (ThreadPoolAdapterState adapterState : threadPoolAdapterStates) {
                    List<ThreadPoolAdapterState> adapterStateList = tenantItemMap.get(adapterState.getThreadPoolKey());
                    if (CollectionUtil.isEmpty(adapterStateList)) {
                        adapterStateList = new ArrayList<>();
                        tenantItemMap.put(adapterState.getThreadPoolKey(), adapterStateList);
                    }
                    Optional<ThreadPoolAdapterState> first = adapterStateList.stream().filter(state -> Objects.equals(state.getClientAddress(), each.getClientAddress())).findFirst();
                    if (!first.isPresent()) {
                        ThreadPoolAdapterState state = new ThreadPoolAdapterState();
                        state.setClientAddress(each.getClientAddress());
                        state.setIdentify(each.getClientIdentify());
                        adapterStateList.add(state);
                    }
                }
            }
        }
    }

    public List<ThreadPoolAdapterRespDTO> query(ThreadPoolAdapterReqDTO requestParameter) {
        List<ThreadPoolAdapterState> actual = Optional.ofNullable(THREAD_POOL_ADAPTER_MAP.get(requestParameter.getMark()))
                .map(each -> each.get(requestParameter.getTenant() + IDENTIFY_SLICER_SYMBOL + requestParameter.getItem()))
                .map(each -> each.get(requestParameter.getThreadPoolKey()))
                .orElse(new ArrayList<>());
        List<String> addressList = actual.stream().map(ThreadPoolAdapterState::getClientAddress).collect(Collectors.toList());
        List<ThreadPoolAdapterRespDTO> result = new ArrayList<>(addressList.size());
        addressList.forEach(each -> {
            String url = StringUtil.newBuilder("http://", each, "/adapter/thread-pool/info");
            Map<String, String> param = new HashMap<>();
            param.put("mark", requestParameter.getMark());
            param.put("threadPoolKey", requestParameter.getThreadPoolKey());
            try {
                String resultStr = HttpUtil.get(url, param);
                if (StringUtil.isNotBlank(resultStr)) {
                    Result<ThreadPoolAdapterRespDTO> restResult = JSONUtil.parseObject(resultStr, new TypeReference<Result<ThreadPoolAdapterRespDTO>>() {
                    });
                    result.add(restResult.getData());
                }
            } catch (Throwable ex) {
                log.error("Failed to get third-party thread pool data.", ex);
            }
        });
        return result;
    }

    public Set<String> queryThreadPoolKey(ThreadPoolAdapterReqDTO requestParameter) {
        Map<String, Map<String, List<ThreadPoolAdapterState>>> threadPoolAdapterStateMap = THREAD_POOL_ADAPTER_MAP.get(requestParameter.getMark());
        if (CollectionUtil.isNotEmpty(threadPoolAdapterStateMap)) {
            String buildKey = requestParameter.getTenant() + IDENTIFY_SLICER_SYMBOL + requestParameter.getItem();
            Map<String, List<ThreadPoolAdapterState>> actual = threadPoolAdapterStateMap.get(buildKey);
            if (CollectionUtil.isNotEmpty(actual)) {
                return actual.keySet();
            }
        }
        return new HashSet<>();
    }

    public static void remove(String identify) {
        synchronized (ThreadPoolAdapterService.class) {
            THREAD_POOL_ADAPTER_MAP.values()
                    .forEach(each -> each.forEach((key, val) -> val.forEach((threadPoolKey, states) -> states.removeIf(adapterState -> Objects.equals(adapterState.getIdentify(), identify)))));
        }
    }

    /**
     * Clear Thread Pool Adapter Cache
     */
    static class ClearThreadPoolAdapterCache implements Observer<String> {

        @Override
        public void accept(ObserverMessage<String> observerMessage) {
            log.info("Clean up the thread-pool adapter cache. Key: {}", observerMessage.message());
            remove(observerMessage.message());
        }
    }
}
