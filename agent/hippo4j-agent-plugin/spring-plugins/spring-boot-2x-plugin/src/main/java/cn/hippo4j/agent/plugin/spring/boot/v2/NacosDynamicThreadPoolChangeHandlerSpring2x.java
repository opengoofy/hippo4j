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

package cn.hippo4j.agent.plugin.spring.boot.v2;

import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.common.executor.ThreadFactoryBuilder;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigParserHandler;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.AbstractConfigThreadPoolDynamicRefresh;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.DEFAULT_NAMESPACE_ID;

/**
 * NacosDynamicThreadPoolChangeHandlerSpring2x is responsible for handling dynamic thread pool
 * configuration changes in a Spring environment by listening to configuration updates from Nacos.
 * <p>
 * This class extends {@link AbstractConfigThreadPoolDynamicRefresh} and implements the logic
 * to register a Nacos listener, handle configuration changes, and dynamically refresh the thread pool
 * properties based on the new configuration.
 * <p>
 * The handler is specifically tailored for use with Spring 2.x and integrates with Hippo4j's
 * dynamic thread pool management system.
 *
 */
public class NacosDynamicThreadPoolChangeHandlerSpring2x extends AbstractConfigThreadPoolDynamicRefresh {

    private static final ILog LOGGER = LogManager.getLogger(NacosDynamicThreadPoolChangeHandlerSpring2x.class);

    /**
     * Registers a listener with Nacos to monitor for changes in the thread pool configuration.
     * <p>
     * This method sets up the Nacos {@link ConfigService} with the server address and namespace
     * from the Spring Boot configuration. It then adds a listener that will receive and process
     * configuration updates, triggering a dynamic refresh of thread pool settings.
     */
    @Override
    public void registerListener() {
        // Retrieve necessary configuration properties
        String configFileType = SpringBootConfig.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;
        String serverAddr = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.SERVER_ADDR;
        String dataId = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.DATA_ID;
        String namespace = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.NAMESPACE.get(0);
        namespace = namespace.equals(DEFAULT_NAMESPACE_ID) ? "" : namespace;
        String group = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.GROUP;
        try {
            // Initialize Nacos ConfigService with the provided properties
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.put(PropertyKeyConst.NAMESPACE, namespace);
            ConfigService configService = NacosFactory.createConfigService(properties);

            // Define the listener to handle configuration changes
            Listener configChangeListener = new Listener() {

                @Override
                public void receiveConfigInfo(String configInfo) {
                    LOGGER.debug("Received configuration: " + configInfo);
                    Map<String, Object> changeValueMap = new HashMap<>();
                    try {
                        // Parse the configuration and map the values to the appropriate keys
                        Map<Object, Object> configInfoMap = ConfigParserHandler.getInstance().parseConfig(configInfo, configFileType);
                        configInfoMap.forEach((key, value) -> {
                            if (key instanceof String) {
                                changeValueMap.put((String) key, value);
                            }
                        });
                    } catch (IOException e) {
                        LOGGER.error(e, "[Hippo4j-Agent] Dynamic thread pool refresher, Failed to resolve configuration. configFileType: {} configInfo: {} ", configFileType, configInfo);
                    }
                    // Trigger the dynamic refresh with the parsed configuration
                    dynamicRefresh(configFileType, configInfo, changeValueMap);
                }

                @Override
                public Executor getExecutor() {
                    return new ScheduledThreadPoolExecutor(
                            1,
                            ThreadFactoryBuilder.builder().daemon(true).prefix("client.dynamic.refresh.agent").build());
                }
            };
            // Add the listener to the Nacos ConfigService
            configService.addListener(dataId, group, configChangeListener);
        } catch (Exception e) {
            LOGGER.error(e, "[Hippo4j-Agent] Dynamic thread pool refresher, add Nacos listener failure. namespace: {} data-id: {} group: {}", namespace, dataId, group);
        }
        LOGGER.info("[Hippo4j-Agent] Dynamic thread pool refresher, added Nacos listener successfully. namespace: {} data-id: {} group: {}", namespace, dataId, group);
    }

    /**
     * Builds and binds the {@link BootstrapConfigProperties} from the given configuration map.
     * <p>
     * This method uses Spring's {@link Binder} to bind the configuration values to an instance
     * of {@link BootstrapConfigProperties}, which can then be used to configure the thread pool
     * dynamically.
     *
     * @param configInfo the configuration map containing properties to bind.
     * @return the bound {@link BootstrapConfigProperties} instance.
     */
    @Override
    public BootstrapConfigProperties buildBootstrapProperties(Map<Object, Object> configInfo) {
        BootstrapConfigProperties bindableBootstrapConfigProperties = new BootstrapConfigProperties();
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
        Binder binder = new Binder(sources);
        return binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(bindableBootstrapConfigProperties)).get();
    }
}
