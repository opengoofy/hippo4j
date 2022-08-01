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

package cn.hippo4j.core.springboot.starter.config;

import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.springboot.starter.config.condition.LogMonitorCondition;
import cn.hippo4j.core.springboot.starter.config.condition.PrometheusMonitorCondition;
import cn.hippo4j.monitor.log.LogMonitorHandler;
import cn.hippo4j.monitor.prometheus.PrometheusMonitorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Monitor handler configuration.
 */
@Configuration(proxyBeanMethods = false)
public class MonitorHandlerConfiguration {

    @Conditional(LogMonitorCondition.class)
    static class EmbeddedLogMonitor {

        @Bean
        public LogMonitorHandler logMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
            return new LogMonitorHandler(threadPoolRunStateHandler);
        }
    }

    @Conditional(PrometheusMonitorCondition.class)
    static class EmbeddedPrometheusMonitor {

        @Bean
        public PrometheusMonitorHandler prometheusMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
            return new PrometheusMonitorHandler(threadPoolRunStateHandler);
        }
    }
}
