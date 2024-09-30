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

package cn.hippo4j.agent.plugin.spring.common.support;

import cn.hippo4j.agent.plugin.spring.common.monitor.MonitorHandlersConfigurator;
import cn.hippo4j.agent.plugin.spring.common.monitor.MonitorMetricEndpoint;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.common.monitor.MonitorCollectTypeEnum;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.MonitorProperties;
import cn.hippo4j.threadpool.monitor.api.ThreadPoolMonitor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES;

/**
 * This class provides support for monitoring dynamic thread pools in an application.
 * It includes methods to initialize and enable monitoring components, and schedules
 * periodic data collection from the thread pools.
 */
public class ThreadPoolMonitorSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolMonitorSupport.class);

    /**
     * A flag used to indicate whether enableThreadPoolMonitorHandler() method has been called,
     * Used to determine whether the ThreadPoolMonitorHandler has been enable
     */
    @Getter
    private static final AtomicBoolean active = new AtomicBoolean(Boolean.FALSE);

    private static final ScheduledExecutorService collectScheduledExecutor = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "client.agent.scheduled.collect.data"));

    private static final List<ThreadPoolMonitor> threadPoolMonitors = new ArrayList<>();

    static {
        // Register the ThreadPoolMonitor service with the ServiceLoaderRegistry
        ServiceLoaderRegistry.register(ThreadPoolMonitor.class);
    }

    /**
     * Enables the dynamic thread pool monitoring handler.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Validates the monitoring configuration from the environment properties.</li>
     *     <li>Initializes monitoring components for the dynamic thread pools.</li>
     *     <li>Exposes metric endpoints, such as Prometheus, if configured.</li>
     *     <li>Schedules periodic collection of metrics from the thread pools.</li>
     * </ul>
     * If the monitoring configuration is invalid or disabled, the method returns without
     * enabling the monitoring handler.
     * </p>
     *
     * @param environment The environment from which the monitoring configuration is loaded.
     */
    public static void enableThreadPoolMonitorHandler(Environment environment) {
        BootstrapConfigProperties properties = BOOTSTRAP_CONFIG_PROPERTIES;
        MonitorProperties monitor = properties.getMonitor();
        if (Objects.isNull(monitor) || !monitor.getEnable() || StringUtil.isBlank(monitor.getThreadPoolTypes()) || StringUtil.isBlank(monitor.getCollectTypes())) {
            return;
        }

        LOGGER.info("[Hippo4j-Agent] Start monitoring the running status of dynamic thread pools.");

        // Initialize monitoring components for the dynamic thread pools
        MonitorHandlersConfigurator.initializeMonitorHandlers(monitor, (ConfigurableEnvironment) environment, threadPoolMonitors);

        // Determine whether the task is successfully enabled
        // return directly if it has been enabled, and do not start the thread pool repeatedly
        if (Boolean.TRUE.equals(active.get()))
            return;

        // Expose metric endpoints based on the configured collect types
        List<String> collectTypes = Arrays.asList(monitor.getCollectTypes().split(","));
        if (collectTypes.contains(MonitorCollectTypeEnum.MICROMETER.getValue())) {
            MonitorMetricEndpoint.startPrometheusEndpoint();
        }

        // Schedule periodic collection of metrics from the thread pools
        Runnable scheduledTask = scheduleRunnable();
        collectScheduledExecutor.scheduleWithFixedDelay(scheduledTask, monitor.getInitialDelay(), monitor.getCollectInterval(), TimeUnit.MILLISECONDS);

        active.set(true);
        if (ThreadPoolExecutorRegistry.getThreadPoolExecutorSize() > 0) {
            LOGGER.info("[Hippo4j-Agent] Dynamic thread pool: [{}]. The dynamic thread pool starts data collection and reporting.", ThreadPoolExecutorRegistry.getThreadPoolExecutorSize());
        }
    }

    /**
     * Returns a Runnable task that collects metrics from the dynamic thread pools.
     * <p>
     * This method is used to create a task that periodically iterates over the
     * registered thread pool monitors and collects their metrics. If an exception
     * occurs during the collection, it is logged.
     * </p>
     *
     * @return A Runnable task that performs the metrics collection.
     */
    private static Runnable scheduleRunnable() {
        return () -> {
            for (ThreadPoolMonitor each : threadPoolMonitors) {
                try {
                    each.collect();
                } catch (Throwable ex) {
                    LOGGER.error("[Hippo4j-Agent] Error monitoring the running status of dynamic thread pool. Type: {}", each.getType(), ex);
                }
            }
        };
    }

}
