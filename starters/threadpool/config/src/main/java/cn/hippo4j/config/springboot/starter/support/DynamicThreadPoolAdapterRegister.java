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

package cn.hippo4j.config.springboot.starter.support;

import cn.hippo4j.threadpool.dynamic.mode.config.properties.AdapterExecutorProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;

/**
 * Dynamic thread-pool adapter register.
 */
@Slf4j
@AllArgsConstructor
public class DynamicThreadPoolAdapterRegister implements InitializingBean {

    private final BootstrapConfigProperties bootstrapConfigProperties;

    public static final Map<String, AdapterExecutorProperties> ADAPTER_EXECUTORS_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        discoverAdapterExecutor();
    }

    public AdapterExecutorProperties discoverAdapterExecutorAndGet(String mark) {
        List<AdapterExecutorProperties> adapterExecutors = bootstrapConfigProperties.getAdapterExecutors();
        for (AdapterExecutorProperties each : adapterExecutors) {
            String buildKey = each.getMark() + IDENTIFY_SLICER_SYMBOL + each.getThreadPoolKey();
            ADAPTER_EXECUTORS_MAP.putIfAbsent(buildKey, each);
        }
        return ADAPTER_EXECUTORS_MAP.get(mark);
    }

    public void discoverAdapterExecutor() {
        Optional<List<AdapterExecutorProperties>> adapterExecutorProperties =
                Optional.ofNullable(bootstrapConfigProperties.getAdapterExecutors());
        adapterExecutorProperties.ifPresent(props -> {
            for (AdapterExecutorProperties each : props) {
                String buildKey = each.getMark() + IDENTIFY_SLICER_SYMBOL + each.getThreadPoolKey();
                ADAPTER_EXECUTORS_MAP.putIfAbsent(buildKey, each);
            }
        });
    }
}
