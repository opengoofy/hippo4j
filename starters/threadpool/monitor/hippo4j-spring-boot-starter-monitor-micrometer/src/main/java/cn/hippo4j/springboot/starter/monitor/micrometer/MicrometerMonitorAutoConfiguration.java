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

package cn.hippo4j.springboot.starter.monitor.micrometer;

import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.enable.MarkerConfiguration;
import cn.hippo4j.monitor.micrometer.AdapterThreadPoolMicrometerMonitorHandler;
import cn.hippo4j.monitor.micrometer.DynamicThreadPoolMicrometerMonitorHandler;
import cn.hippo4j.monitor.micrometer.WebThreadPoolMicrometerMonitorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer monitor auto configuration.
 */
@Configuration
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.collect-types:}'.contains('micrometer')")
public class MicrometerMonitorAutoConfiguration {

    @Bean
    @ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.thread-pool-types:}'.contains('dynamic')")
    public DynamicThreadPoolMicrometerMonitorHandler dynamicThreadPoolMicrometerMonitorHandler(ThreadPoolRunStateHandler handler) {
        return new DynamicThreadPoolMicrometerMonitorHandler(handler);
    }

    @Bean
    @ConditionalOnBean(WebThreadPoolService.class)
    @ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.thread-pool-types:}'.contains('web')")
    public WebThreadPoolMicrometerMonitorHandler webThreadPoolMicrometerMonitorHandler() {
        return new WebThreadPoolMicrometerMonitorHandler();
    }

    @Bean
    @ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.thread-pool-types:}'.contains('adapter')")
    public AdapterThreadPoolMicrometerMonitorHandler adapterThreadPoolMicrometerMonitorHandler() {
        return new AdapterThreadPoolMicrometerMonitorHandler();
    }
}
