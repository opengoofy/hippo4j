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

package cn.hippo4j.adapter.alibaba.dubbo;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.store.DataStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * Alibaba Dubbo thread-pool adapter.
 */
@Slf4j
public class AlibabaDubboThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private final Map<String, ThreadPoolExecutor> dubboProtocolExecutor = new HashMap<>();

    @Override
    public String mark() {
        return "AlibabaDubbo";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState threadPoolAdapterState = new ThreadPoolAdapterState();
        ThreadPoolExecutor executor = dubboProtocolExecutor.get(identify);
        if (executor == null) {
            log.warn("[{}] Alibaba Dubbo consuming thread pool not found.", identify);
            return threadPoolAdapterState;
        }
        threadPoolAdapterState.setThreadPoolKey(identify);
        threadPoolAdapterState.setCoreSize(executor.getCorePoolSize());
        threadPoolAdapterState.setMaximumSize(executor.getMaximumPoolSize());
        return threadPoolAdapterState;
    }

    @Override
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> threadPoolAdapterStates = new ArrayList<>();
        dubboProtocolExecutor.forEach((key, val) -> threadPoolAdapterStates.add(getThreadPoolState(String.valueOf(key))));
        return threadPoolAdapterStates;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        ThreadPoolExecutor executor = dubboProtocolExecutor.get(threadPoolAdapterParameter.getThreadPoolKey());
        if (executor == null) {
            log.warn("[{}] Alibaba Dubbo consuming thread pool not found.", threadPoolKey);
            return false;
        }
        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, threadPoolAdapterParameter.getCorePoolSize(), threadPoolAdapterParameter.getMaximumPoolSize());
        log.info("[{}] Alibaba Dubbo consumption thread pool parameter change. coreSize: {}, maximumSize: {}",
                threadPoolKey,
                String.format(CHANGE_DELIMITER, originalCoreSize, executor.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, originalMaximumPoolSize, executor.getMaximumPoolSize()));
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        String poolKey = ExecutorService.class.getName();
        try {
            DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
            Map<String, Object> executors = dataStore.get(poolKey);
            executors.forEach((key, value) -> dubboProtocolExecutor.put(key, (ThreadPoolExecutor) value));
        } catch (Exception ex) {
            log.error("Failed to get Alibaba Dubbo protocol thread pool", ex);
        }
    }
}
