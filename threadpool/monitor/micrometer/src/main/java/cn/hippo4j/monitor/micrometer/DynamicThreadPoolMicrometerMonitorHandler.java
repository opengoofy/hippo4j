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

import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.monitor.base.AbstractDynamicThreadPoolMonitor;
import cn.hippo4j.threadpool.monitor.api.ThreadPoolMonitor;
import cn.hippo4j.threadpool.monitor.support.MonitorTypeEnum;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic thread-pool micrometer monitor handler.
 */
public class DynamicThreadPoolMicrometerMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    private static final String METRIC_NAME_PREFIX = "dynamic.thread-pool";

    private static final String DYNAMIC_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";

    private static final String APPLICATION_NAME_TAG = "application.name";

    private final Map<String, ThreadPoolRunStateInfo> runStateCache = new ConcurrentHashMap<>();

    public DynamicThreadPoolMicrometerMonitorHandler(ThreadPoolRunStateHandler handler) {
        super(handler);
    }

    static {
        ServiceLoaderRegistry.register(ThreadPoolMonitor.class);
    }

    @Override
    protected void execute(ThreadPoolRunStateInfo poolRunStateInfo) {
        ThreadPoolRunStateInfo stateInfo = runStateCache.get(poolRunStateInfo.getTpId());
        if (stateInfo != null) {
            BeanUtil.convert(poolRunStateInfo, stateInfo);
        } else {
            runStateCache.put(poolRunStateInfo.getTpId(), poolRunStateInfo);
        }
        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", "application");
        Iterable<Tag> tags = CollectionUtil.newArrayList(
                Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, poolRunStateInfo.getTpId()),
                Tag.of(APPLICATION_NAME_TAG, applicationName));
        Metrics.gauge(metricName("current.load"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getSimpleCurrentLoad);
        Metrics.gauge(metricName("peak.load"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getSimplePeakLoad);
        Metrics.gauge(metricName("core.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getCoreSize);
        Metrics.gauge(metricName("maximum.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getMaximumSize);
        Metrics.gauge(metricName("current.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getPoolSize);
        Metrics.gauge(metricName("largest.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getLargestPoolSize);
        Metrics.gauge(metricName("active.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getActiveSize);
        Metrics.gauge(metricName("queue.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueSize);
        Metrics.gauge(metricName("queue.capacity"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueCapacity);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueRemainingCapacity);
        Metrics.gauge(metricName("completed.task.count"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getCompletedTaskCount);
        Metrics.gauge(metricName("reject.count"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getRejectCount);
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    @Override
    public String getType() {
        return MonitorTypeEnum.MICROMETER.name().toLowerCase();
    }
}
