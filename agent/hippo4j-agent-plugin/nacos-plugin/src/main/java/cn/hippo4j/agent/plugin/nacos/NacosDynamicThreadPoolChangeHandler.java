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

package cn.hippo4j.agent.plugin.nacos;

import cn.hippo4j.agent.plugin.spring.common.conf.NacosCloudConfig;
import cn.hippo4j.agent.plugin.spring.common.conf.NacosConfig;
import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.agent.plugin.spring.common.toolkit.SpringPropertyBinder;
import cn.hippo4j.common.executor.ThreadFactoryBuilder;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigParserHandler;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.AbstractConfigThreadPoolDynamicRefresh;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.DEFAULT_NAMESPACE_ID;

/**
 * NacosDynamicThreadPoolChangeHandler is responsible for handling dynamic thread pool
 * configuration changes in a Spring environment by listening to configuration updates from Nacos.
 * <p>
 * This class extends {@link AbstractConfigThreadPoolDynamicRefresh} and implements the logic
 * to register a Nacos listener, handle configuration changes, and dynamically refresh the thread pool
 * properties based on the new configuration.
 * <p>
 */
public class NacosDynamicThreadPoolChangeHandler extends AbstractConfigThreadPoolDynamicRefresh {

    private static final ILog LOGGER = LogManager.getLogger(NacosDynamicThreadPoolChangeHandler.class);

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
        String serverAddr = Optional.ofNullable(NacosCloudConfig.Spring.Cloud.Nacos.Config.SERVER_ADDR).filter(s -> !StringUtil.isEmpty(s))
                .orElse(Optional.ofNullable(NacosConfig.Nacos.Config.SERVER_ADDR).filter(s -> !StringUtil.isEmpty(s))
                        .orElse(""));
        if (StringUtil.isEmpty(serverAddr)) {
            LOGGER.error("[Hippo4j-Agent] add Nacos listener failure. Nacos Registry address not configured");
            return;
        }
        String dataId = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.DATA_ID;
        String group = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.GROUP;
        String namespace = SpringBootConfig.Spring.Dynamic.Thread_Pool.Nacos.NAMESPACE.get(0);
        namespace = namespace.equals(DEFAULT_NAMESPACE_ID) ? "" : namespace;
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
                    return new ScheduledThreadPoolExecutor(1, ThreadFactoryBuilder.builder().daemon(true).prefix("client.dynamic.refresh.agent").build());
                }
            };
            // Add the listener to the Nacos ConfigService
            configService.addListener(dataId, group, configChangeListener);
            LOGGER.info("[Hippo4j-Agent] Dynamic thread pool refresher, add Nacos listener successfully. serverAddr: {} namespace: {} data-id: {} group: {}", serverAddr, namespace, dataId, group);
        } catch (Exception e) {
            LOGGER.error(e, "[Hippo4j-Agent] Dynamic thread pool refresher, add Nacos listener failure. serverAddr: {} namespace: {} data-id: {} group: {}", serverAddr, namespace, dataId, group);
        }
    }

    /**
     * Builds and binds the {@link BootstrapConfigProperties} from the given configuration map.
     * <p>
     * This method uses SpringPropertyBinder to bind the configuration values to an instance
     * of {@link BootstrapConfigProperties}, which can then be used to configure the thread pool
     * dynamically.
     *
     * @param configInfo the configuration map containing properties to bind.
     * @return the bound {@link BootstrapConfigProperties} instance.
     */
    @Override
    public BootstrapConfigProperties buildBootstrapProperties(Map<Object, Object> configInfo) {
        BootstrapConfigProperties bindableBootstrapConfigProperties = SpringPropertyBinder.bindProperties(configInfo, BootstrapConfigProperties.PREFIX, BootstrapConfigProperties.class);
        return bindableBootstrapConfigProperties;
    }
}
