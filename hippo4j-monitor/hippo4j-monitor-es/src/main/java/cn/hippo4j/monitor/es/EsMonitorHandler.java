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

package cn.hippo4j.monitor.es;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hutool.core.bean.BeanUtil;
import com.example.monitor.base.AbstractDynamicThreadPoolMonitor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Prometheus monitor handler.
 */
public class EsMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    private final Map<String, ThreadPoolRunStateInfo> RUN_STATE_CACHE = Maps.newConcurrentMap();

    public EsMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        super(threadPoolRunStateHandler);
    }

    @Override
    protected void execute(ThreadPoolRunStateInfo poolRunStateInfo) {
        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        RestHighLevelClient esClient = ApplicationContextHolder.getBean(RestHighLevelClient.class);

        String applicationName = environment.getProperty("spring.application.name", "application");
        // Iterable<Tag> tags = Lists.newArrayList(
        // Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, poolRunStateInfo.getTpId()),
        // Tag.of(APPLICATION_NAME_TAG, applicationName));
        // // load
        // Metrics.gauge(metricName("current.load"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getSimpleCurrentLoad);
        // Metrics.gauge(metricName("peak.load"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getSimplePeakLoad);
        // // thread pool
        // Metrics.gauge(metricName("core.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getCoreSize);
        // Metrics.gauge(metricName("maximum.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getMaximumSize);
        // Metrics.gauge(metricName("current.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getPoolSize);
        // Metrics.gauge(metricName("largest.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getLargestPoolSize);
        // Metrics.gauge(metricName("active.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getActiveSize);
        // // queue
        // Metrics.gauge(metricName("queue.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueSize);
        // Metrics.gauge(metricName("queue.capacity"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueCapacity);
        // Metrics.gauge(metricName("queue.remaining.capacity"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueRemainingCapacity);
        // // other
        // Metrics.gauge(metricName("completed.task.count"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getCompletedTaskCount);
        // Metrics.gauge(metricName("reject.count"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getRejectCount);
    }

    @Override
    public String getType() {
        return "es";
    }
}
