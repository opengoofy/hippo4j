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

package cn.hippo4j.monitor.micrometer;

import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.monitor.base.AbstractAdapterThreadPoolMonitor;
import cn.hippo4j.threadpool.monitor.support.MonitorTypeEnum;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter thread-pool micrometer monitor handler.
 */
public class AdapterThreadPoolMicrometerMonitorHandler extends AbstractAdapterThreadPoolMonitor {

    private static final String METRIC_NAME_PREFIX = "adapter.thread-pool";

    private static final String ADAPTER_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";

    private static final String APPLICATION_NAME_TAG = "application.name";

    private final Map<String, ThreadPoolAdapterState> runStateCache = new ConcurrentHashMap<>();

    @Override
    protected void execute(ThreadPoolAdapterState threadPoolAdapterState) {
        ThreadPoolAdapterState stateInfo = runStateCache.get(threadPoolAdapterState.getThreadPoolKey());
        if (stateInfo != null) {
            BeanUtil.convert(threadPoolAdapterState, stateInfo);
        } else {
            runStateCache.put(threadPoolAdapterState.getThreadPoolKey(), threadPoolAdapterState);
        }
        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", "application");
        Iterable<Tag> tags = CollectionUtil.newArrayList(
                Tag.of(ADAPTER_THREAD_POOL_ID_TAG, threadPoolAdapterState.getThreadPoolKey()),
                Tag.of(APPLICATION_NAME_TAG, applicationName));
        Metrics.gauge(metricName("core.size"), tags, threadPoolAdapterState, ThreadPoolAdapterState::getCoreSize);
        Metrics.gauge(metricName("maximum.size"), tags, threadPoolAdapterState, ThreadPoolAdapterState::getMaximumSize);
        if (threadPoolAdapterState.getBlockingQueueCapacity() != null) {
            Metrics.gauge(metricName("queue.capacity"), tags, threadPoolAdapterState, ThreadPoolAdapterState::getBlockingQueueCapacity);
        }
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    @Override
    public String getType() {
        return MonitorTypeEnum.MICROMETER.name().toLowerCase();
    }
}
