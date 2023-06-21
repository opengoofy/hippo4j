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

package cn.hippo4j.springboot.starter.monitor.local.log;

import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.enable.MarkerConfiguration;
import cn.hippo4j.monitor.local.log.AdapterThreadPoolLocalLogMonitorHandler;
import cn.hippo4j.monitor.local.log.DynamicThreadPoolLocalLogMonitorHandler;
import cn.hippo4j.monitor.local.log.WebThreadPoolLocalLogMonitorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Local log monitor auto configuration.
 */
@Configuration
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.collect-types:}'.contains('log')")
public class LocalLogMonitorAutoConfiguration {

    @Bean
    @ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.thread-pool-types:}'.contains('dynamic')")
    public DynamicThreadPoolLocalLogMonitorHandler dynamicThreadPoolLocalLogMonitorHandler(ThreadPoolRunStateHandler handler) {
        return new DynamicThreadPoolLocalLogMonitorHandler(handler);
    }

    @Bean
    @ConditionalOnBean(WebThreadPoolService.class)
    @ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.thread-pool-types:}'.contains('web')")
    public WebThreadPoolLocalLogMonitorHandler webThreadPoolLocalLogMonitorHandler() {
        return new WebThreadPoolLocalLogMonitorHandler();
    }

    @Bean
    @ConditionalOnExpression("'${spring.dynamic.thread-pool.monitor.thread-pool-types:}'.contains('adapter')")
    public AdapterThreadPoolLocalLogMonitorHandler adapterThreadPoolLocalLogMonitorHandler() {
        return new AdapterThreadPoolLocalLogMonitorHandler();
    }
}
