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

package cn.hippo4j.agent.plugin.spring.common.monitor;

import cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader;
import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.common.monitor.MonitorCollectTypeEnum;
import cn.hippo4j.common.monitor.MonitorHandlerTypeEnum;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.monitor.elasticsearch.AdapterThreadPoolElasticSearchMonitorHandler;
import cn.hippo4j.monitor.elasticsearch.DynamicThreadPoolElasticSearchMonitorHandler;
import cn.hippo4j.monitor.elasticsearch.WebThreadPoolElasticSearchMonitorHandler;
import cn.hippo4j.monitor.local.log.AdapterThreadPoolLocalLogMonitorHandler;
import cn.hippo4j.monitor.local.log.DynamicThreadPoolLocalLogMonitorHandler;
import cn.hippo4j.monitor.local.log.WebThreadPoolLocalLogMonitorHandler;
import cn.hippo4j.monitor.micrometer.AdapterThreadPoolMicrometerMonitorHandler;
import cn.hippo4j.monitor.micrometer.DynamicThreadPoolMicrometerMonitorHandler;
import cn.hippo4j.monitor.micrometer.WebThreadPoolMicrometerMonitorHandler;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.MonitorProperties;
import cn.hippo4j.threadpool.monitor.api.ThreadPoolMonitor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * This class is responsible for configuring and initializing monitoring handlers
 * for various types of thread pools. It maps specific monitoring types (e.g., Micrometer,
 * Log, Elasticsearch) to their corresponding handler initializers and manages the
 * setup process based on the provided configuration.
 */
public class MonitorHandlersConfigurator {

    private static final ILog LOGGER = LogManager.getLogger(MonitorHandlersConfigurator.class);

    // Maps thread pool types to their corresponding handler constructors
    private static final Map<String, BiConsumer<MonitorHandlerTypeEnum, MonitorHandlerContext>> handlerMap = new HashMap<>();

    static {
        // Initialize the handler map with specific monitoring types
        handlerMap.put(MonitorCollectTypeEnum.MICROMETER.getValue(), MonitorHandlersConfigurator::handleMicrometer);
        handlerMap.put(MonitorCollectTypeEnum.LOG.getValue(), MonitorHandlersConfigurator::handleLog);
        handlerMap.put(MonitorCollectTypeEnum.ELASTICSEARCH.getValue(), MonitorHandlersConfigurator::handleElasticSearch);
    }

    /**
     * Initializes the monitoring handlers based on the provided monitoring configuration.
     * <p>
     * This method performs the following tasks:
     * <ul>
     *     <li>Parses the configured monitoring types and thread pool types.</li>
     *     <li>Initializes a monitoring context with the necessary thread pool monitors and state handler.</li>
     *     <li>For each configured monitoring type, invokes the corresponding handler initializer
     *     for each relevant thread pool type.</li>
     *     <li>Logs a warning if an unrecognized monitoring type is encountered.</li>
     *     <li>Registers and adds thread pool monitors that match the configured monitoring types.</li>
     * </ul>
     *
     * @param monitor            The monitoring properties configuration.
     * @param environment        The application environment from which additional configuration can be loaded.
     * @param threadPoolMonitors A list to hold the initialized thread pool monitors.
     */
    public static void initializeMonitorHandlers(MonitorProperties monitor, ConfigurableEnvironment environment, List<ThreadPoolMonitor> threadPoolMonitors) {
        List<String> collectTypes = Arrays.asList(monitor.getCollectTypes().split(","));
        List<String> threadPoolTypes = Arrays.asList(monitor.getThreadPoolTypes().split(","));
        ThreadPoolRunStateHandler threadPoolRunStateHandler = new ThreadPoolRunStateHandler(
                SpringPropertiesLoader.inetUtils, environment);

        MonitorHandlerContext context = new MonitorHandlerContext(threadPoolMonitors, threadPoolRunStateHandler);

        // Initialize handlers for each configured monitoring type and thread pool type
        for (String collectType : collectTypes) {
            if (handlerMap.containsKey(collectType)) {
                for (MonitorHandlerTypeEnum type : MonitorHandlerTypeEnum.values()) {
                    if (threadPoolTypes.contains(type.name().toLowerCase())) {
                        handlerMap.get(collectType).accept(type, context);
                    }
                }
            } else {
                LOGGER.warn("[Hippo4j-Agent] MonitorConfigurator initialize Unrecognized collect type: [{}]", collectType);
            }
        }

        // Register and add dynamic thread pool monitors matching the configured types
        Collection<ThreadPoolMonitor> dynamicThreadPoolMonitors = ServiceLoaderRegistry.getSingletonServiceInstances(ThreadPoolMonitor.class);
        dynamicThreadPoolMonitors.stream().filter(each -> collectTypes.contains(each.getType())).forEach(threadPoolMonitors::add);
    }

    /**
     * Initializes Micrometer-based monitoring handlers for the specified thread pool type.
     *
     * @param type    The type of thread pool to be monitored.
     * @param context The context containing the monitors and state handler.
     */
    private static void handleMicrometer(MonitorHandlerTypeEnum type, MonitorHandlerContext context) {
        switch (type) {
            case DYNAMIC:
                context.monitors.add(new DynamicThreadPoolMicrometerMonitorHandler(context.threadPoolRunStateHandler));
                break;
            case WEB:
                context.monitors.add(new WebThreadPoolMicrometerMonitorHandler());
                break;
            case ADAPTER:
                context.monitors.add(new AdapterThreadPoolMicrometerMonitorHandler());
                break;
        }
    }

    /**
     * Initializes Log-based monitoring handlers for the specified thread pool type.
     *
     * @param type    The type of thread pool to be monitored.
     * @param context The context containing the monitors and state handler.
     */
    private static void handleLog(MonitorHandlerTypeEnum type, MonitorHandlerContext context) {
        switch (type) {
            case DYNAMIC:
                context.monitors.add(new DynamicThreadPoolLocalLogMonitorHandler(context.threadPoolRunStateHandler));
                break;
            case WEB:
                context.monitors.add(new WebThreadPoolLocalLogMonitorHandler());
                break;
            case ADAPTER:
                context.monitors.add(new AdapterThreadPoolLocalLogMonitorHandler());
                break;
        }
    }

    /**
     * Initializes Elasticsearch-based monitoring handlers for the specified thread pool type.
     *
     * @param type    The type of thread pool to be monitored.
     * @param context The context containing the monitors and state handler.
     */
    private static void handleElasticSearch(MonitorHandlerTypeEnum type, MonitorHandlerContext context) {
        switch (type) {
            case DYNAMIC:
                context.monitors.add(new DynamicThreadPoolElasticSearchMonitorHandler(context.threadPoolRunStateHandler));
                break;
            case WEB:
                context.monitors.add(new WebThreadPoolElasticSearchMonitorHandler());
                break;
            case ADAPTER:
                context.monitors.add(new AdapterThreadPoolElasticSearchMonitorHandler());
                break;
        }
    }

    /**
     * A helper class to manage the context in which monitoring handlers are initialized.
     */
    private static class MonitorHandlerContext {

        List<ThreadPoolMonitor> monitors;
        ThreadPoolRunStateHandler threadPoolRunStateHandler;

        MonitorHandlerContext(List<ThreadPoolMonitor> monitors, ThreadPoolRunStateHandler handler) {
            this.monitors = monitors;
            this.threadPoolRunStateHandler = handler;
        }
    }
}
