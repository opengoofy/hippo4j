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

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.monitor.base.AbstractWebThreadPoolMonitor;
import cn.hippo4j.threadpool.monitor.support.MonitorTypeEnum;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Web thread-pool micrometer monitor handler.
 */
public class WebThreadPoolMicrometerMonitorHandler extends AbstractWebThreadPoolMonitor {

    private static final String METRIC_NAME_PREFIX = "web.thread-pool";

    private static final String APPLICATION_NAME_TAG = "application.name";

    private final Map<String, ThreadPoolRunStateInfo> runStateCache = new ConcurrentHashMap<>();

    @Override
    protected void execute(ThreadPoolRunStateInfo webThreadPoolRunStateInfo) {
        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", "application");
        ThreadPoolRunStateInfo stateInfo = runStateCache.get(applicationName);
        if (stateInfo != null) {
            BeanUtil.convert(webThreadPoolRunStateInfo, stateInfo);
        } else {
            runStateCache.put(applicationName, webThreadPoolRunStateInfo);
        }
        Iterable<Tag> tags = CollectionUtil.newArrayList(Tag.of(APPLICATION_NAME_TAG, applicationName));
        Metrics.gauge(metricName("current.load"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getSimpleCurrentLoad);
        Metrics.gauge(metricName("peak.load"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getSimplePeakLoad);
        Metrics.gauge(metricName("core.size"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getCoreSize);
        Metrics.gauge(metricName("maximum.size"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getMaximumSize);
        Metrics.gauge(metricName("current.size"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getPoolSize);
        Metrics.gauge(metricName("largest.size"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getLargestPoolSize);
        Metrics.gauge(metricName("active.size"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getActiveSize);
        Metrics.gauge(metricName("queue.size"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getQueueSize);
        Metrics.gauge(metricName("queue.capacity"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getQueueCapacity);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getQueueRemainingCapacity);
        Metrics.gauge(metricName("completed.task.count"), tags, webThreadPoolRunStateInfo, ThreadPoolRunStateInfo::getCompletedTaskCount);
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    @Override
    public String getType() {
        return MonitorTypeEnum.MICROMETER.name().toLowerCase();
    }
}
