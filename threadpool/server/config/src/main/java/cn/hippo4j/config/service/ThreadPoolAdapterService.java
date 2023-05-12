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

import cn.hippo4j.adapter.base.ThreadPoolAdapterApi;
import cn.hippo4j.adapter.base.ThreadPoolAdapterCacheConfig;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.constant.ConfigModifyTypeConstants;
import cn.hippo4j.common.design.observer.AbstractSubjectCenter;
import cn.hippo4j.common.design.observer.Observer;
import cn.hippo4j.common.design.observer.ObserverMessage;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.config.model.biz.adapter.ThreadPoolAdapterReqDTO;
import cn.hippo4j.config.model.biz.adapter.ThreadPoolAdapterRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifySaveReqDTO;
import cn.hippo4j.config.verify.ConfigModificationVerifyServiceChoose;
import cn.hippo4j.rpc.support.NettyProxyCenter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ThreadPoolAdapterService {

    /**
     * Map&lt;mark, Map&lt;tenantItem, Map&lt;threadPoolKey, List&lt;ThreadPoolAdapterState&gt;&gt;&gt;&gt;
     */
    private static final Map<String, Map<String, Map<String, List<ThreadPoolAdapterState>>>> THREAD_POOL_ADAPTER_MAP = new ConcurrentHashMap<>();

    private static final Class<ThreadPoolAdapterApi> CLASS = ThreadPoolAdapterApi.class;

    private final ConfigModificationVerifyServiceChoose configModificationVerifyServiceChoose;

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
                    String clientAddress = each.getClientAddress();
                    String localServerAddress = each.getLocalServerAddress();
                    Optional<ThreadPoolAdapterState> first = adapterStateList.stream().filter(state -> Objects.equals(state.getClientAddress(), clientAddress)).findFirst();
                    if (!first.isPresent()) {
                        ThreadPoolAdapterState state = new ThreadPoolAdapterState();
                        state.setClientAddress(clientAddress);
                        state.setIdentify(each.getClientIdentify());
                        state.setLocalServerAddress(localServerAddress);
                        adapterStateList.add(state);
                        NettyProxyCenter.getProxy(CLASS, localServerAddress);
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
        List<String> addressList = actual.stream().map(ThreadPoolAdapterState::getLocalServerAddress).collect(Collectors.toList());
        List<ThreadPoolAdapterRespDTO> result = new ArrayList<>(addressList.size());
        addressList.forEach(each -> {
            try {
                ThreadPoolAdapterApi adapterApi = NettyProxyCenter.getProxy(CLASS, each);
                ThreadPoolAdapterParameter parameter = new ThreadPoolAdapterParameter();
                parameter.setThreadPoolKey(requestParameter.getThreadPoolKey());
                parameter.setMark(requestParameter.getMark());
                Result<ThreadPoolAdapterState> adapterThreadPool = adapterApi.getAdapterThreadPool(parameter);
                result.add(BeanUtil.convert(adapterThreadPool.getData(), ThreadPoolAdapterRespDTO.class));
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

    /**
     * If the user's authority is the administrator authority, modify it directly, if not, add it to the database and wait for review
     *
     * @param requestParameter ThreadPoolAdapterReqDTO
     */
    public void updateThreadPool(ThreadPoolAdapterReqDTO requestParameter) {
        if (UserContext.getUserRole().equals("ROLE_ADMIN")) {
            List<ThreadPoolAdapterState> actual = Optional.ofNullable(THREAD_POOL_ADAPTER_MAP.get(requestParameter.getMark()))
                    .map(each -> each.get(requestParameter.getTenant() + IDENTIFY_SLICER_SYMBOL + requestParameter.getItem()))
                    .map(each -> each.get(requestParameter.getThreadPoolKey()))
                    .orElse(new ArrayList<>());
            actual.forEach(t -> {
                String localServerAddress = t.getLocalServerAddress();
                ThreadPoolAdapterApi adapterApi = NettyProxyCenter.getProxy(CLASS, localServerAddress);
                ThreadPoolAdapterParameter parameter = BeanUtil.convert(requestParameter, ThreadPoolAdapterParameter.class);
                adapterApi.updateAdapterThreadPool(parameter);
            });
        } else {
            ConfigModifySaveReqDTO modifySaveReqDTO = BeanUtil.convert(requestParameter, ConfigModifySaveReqDTO.class);
            modifySaveReqDTO.setModifyUser(UserContext.getUserName());
            modifySaveReqDTO.setTenantId(requestParameter.getTenant());
            modifySaveReqDTO.setItemId(requestParameter.getItem());
            modifySaveReqDTO.setTpId(requestParameter.getThreadPoolKey());
            modifySaveReqDTO.setType(ConfigModifyTypeConstants.ADAPTER_THREAD_POOL);
            configModificationVerifyServiceChoose.choose(modifySaveReqDTO.getType()).saveConfigModifyApplication(modifySaveReqDTO);
        }
    }

    public static void remove(String identify) {
        synchronized (ThreadPoolAdapterService.class) {
            THREAD_POOL_ADAPTER_MAP.values().forEach(each -> each.forEach((key, val) -> val.forEach((threadPoolKey, states) -> {
                states.stream()
                        .filter(s -> Objects.equals(s.getIdentify(), identify))
                        .forEach(t -> NettyProxyCenter.removeProxy(CLASS, t.getLocalServerAddress()));
                states.removeIf(s -> Objects.equals(s.getIdentify(), identify));
            })));
        }
    }

    static class ClearThreadPoolAdapterCache implements Observer<String> {

        @Override
        public void accept(ObserverMessage<String> observerMessage) {
            log.info("Clean up the thread-pool adapter cache. Key: {}", observerMessage.message());
            remove(observerMessage.message());
        }
    }
}
