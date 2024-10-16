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

import cn.hippo4j.agent.plugin.spring.common.alarm.AgentModeNotifyConfigBuilder;
import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.agent.plugin.spring.common.toolkit.SpringPropertyBinder;
import cn.hippo4j.common.propertie.EnvironmentProperties;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.core.toolkit.inet.InetUtilsProperties;
import cn.hippo4j.threadpool.alarm.handler.DefaultThreadPoolCheckAlarmHandler;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.platform.DingSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.LarkSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.WeChatSendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import cn.hippo4j.threadpool.message.core.service.DefaultThreadPoolConfigChangeHandler;
import cn.hippo4j.threadpool.message.core.service.SendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolBaseSendMessageService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES;

/**
 * The {@code ThreadPoolCheckAlarmSupport} class provides functionality to enable and configure
 * a thread pool check alarm handler. This is typically used to monitor thread pools for potential
 * issues and send notifications based on the configured alert mechanisms.
 */
public class ThreadPoolCheckAlarmSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolCheckAlarmSupport.class);

    @Getter
    private static ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService;

    @Getter
    private static DefaultThreadPoolConfigChangeHandler threadPoolConfigChangeHandler;

    @Getter
    private static AgentModeNotifyConfigBuilder agentNotifyConfigBuilder;

    private static DefaultThreadPoolCheckAlarmHandler checkAlarmHandler;

    /**
     * Enables the thread pool check alarm handler if the corresponding configuration property is set to {@code true}.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Checks the value of the {@code enable} property in the bootstrap configuration. If it is {@code true}, it proceeds.</li>
     *     <li>Initializes environment properties needed for the monitoring process.</li>
     *     <li>Creates an instance of {@link AlarmControlHandler} and {@link ThreadPoolBaseSendMessageService} with necessary dependencies.</li>
     *     <li>Initializes and registers message handlers and notification configurations.</li>
     *     <li>Creates an instance of {@link DefaultThreadPoolCheckAlarmHandler} and schedules it to start monitoring the thread pool.</li>
     * </ul>
     */
    public static void enableThreadPoolCheckAlarmHandler() {
        // Check if the thread pool checker is enabled in the bootstrap configuration properties
        if (Boolean.TRUE.equals(BOOTSTRAP_CONFIG_PROPERTIES.getEnable())) {

            // Initialize EnvironmentProperties
            initializeEnvironmentProperties();

            // Initialize the AlarmControlHandler and ThreadPoolBaseSendMessageService
            AlarmControlHandler alarmControlHandler = new AlarmControlHandler();
            threadPoolBaseSendMessageService = createThreadPoolBaseSendMessageService(alarmControlHandler);
            threadPoolConfigChangeHandler = new DefaultThreadPoolConfigChangeHandler(threadPoolBaseSendMessageService);

            // Initialize the alarm platform information
            initializeSendMessageHandlers(threadPoolBaseSendMessageService, alarmControlHandler);

            // Execute scheduled task to check an alarm
            scheduleExecute(threadPoolBaseSendMessageService);

            LOGGER.info("[Hippo4j-Agent] Start Check AlarmHandler the running status of dynamic thread pools.");

        }
    }

    /**
     * Initializes environment properties used for thread pool monitoring.
     * <p>
     * This method sets the state check interval, item ID, application name, and active profile from the bootstrap configuration.
     */
    private static void initializeEnvironmentProperties() {
        EnvironmentProperties.checkStateInterval = Long.valueOf(BOOTSTRAP_CONFIG_PROPERTIES.getCheckStateInterval());
        EnvironmentProperties.itemId = BOOTSTRAP_CONFIG_PROPERTIES.getItemId();
        EnvironmentProperties.applicationName = SpringBootConfig.Spring.Application.name;
        EnvironmentProperties.active = SpringBootConfig.Spring.Profiles.active;
        ConfigurableEnvironment environment = ApplicationContextHolder.getBean(ConfigurableEnvironment.class);
        InetUtilsProperties inetUtilsProperties = SpringPropertyBinder.bindProperties(environment, InetUtilsProperties.PREFIX, InetUtilsProperties.class);
        SpringPropertiesLoader.inetUtils = new InetUtils(inetUtilsProperties);
        IdentifyUtil.generate(environment, SpringPropertiesLoader.inetUtils);
    }

    /**
     * Creates and returns a new instance of {@link ThreadPoolBaseSendMessageService} with the specified {@link AlarmControlHandler}.
     *
     * @param alarmControlHandler The {@link AlarmControlHandler} used to control and handle alarms.
     * @return A new instance of {@link ThreadPoolBaseSendMessageService}.
     */
    private static ThreadPoolBaseSendMessageService createThreadPoolBaseSendMessageService(AlarmControlHandler alarmControlHandler) {
        return new ThreadPoolBaseSendMessageService(alarmControlHandler);
    }

    /**
     * Initializes and registers the message handlers and notification configurations in the specified
     * {@link ThreadPoolBaseSendMessageService}.
     * <p>
     * This method creates instances of various {@link SendMessageHandler} implementations and registers them.
     * It also constructs and registers notification configurations using the {@link AgentModeNotifyConfigBuilder}.
     *
     * @param threadPoolBaseSendMessageService The {@link ThreadPoolBaseSendMessageService} in which message handlers and notification configurations will be registered.
     * @param alarmControlHandler              The {@link AlarmControlHandler} used to handle alarms and notifications.
     */
    private static void initializeSendMessageHandlers(ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService, AlarmControlHandler alarmControlHandler) {
        // Initialize message handlers
        DingSendMessageHandler dingSendMessageHandler = new DingSendMessageHandler();
        WeChatSendMessageHandler weChatSendMessageHandler = new WeChatSendMessageHandler();
        LarkSendMessageHandler larkSendMessageHandler = new LarkSendMessageHandler();

        // Register message handlers
        threadPoolBaseSendMessageService.getSendMessageHandlers().put(dingSendMessageHandler.getType(), dingSendMessageHandler);
        threadPoolBaseSendMessageService.getSendMessageHandlers().put(weChatSendMessageHandler.getType(), weChatSendMessageHandler);
        threadPoolBaseSendMessageService.getSendMessageHandlers().put(larkSendMessageHandler.getType(), larkSendMessageHandler);

        // Construct and register notification configurations
        // TODO : register notify config for web , null Can be replaced with tomcat, jetty, undertow, etc. implementation classes
        agentNotifyConfigBuilder = new AgentModeNotifyConfigBuilder(alarmControlHandler, null);
        Map<String, List<NotifyConfigDTO>> notifyConfigs = agentNotifyConfigBuilder.buildNotify();
        threadPoolBaseSendMessageService.getNotifyConfigs().putAll(notifyConfigs);
    }

    // 启动或重新启动检查任务
    public static void scheduleExecute(ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService) {
        // If a task is already running, cancel it first
        if (checkAlarmHandler != null) {
            // Shut down the thread pool and prepare to regenerate the listener thread pool
            checkAlarmHandler.destroyScheduleExecute();
        }
        // Initialize the thread pool check alarm handler with necessary services
        checkAlarmHandler = new DefaultThreadPoolCheckAlarmHandler(threadPoolBaseSendMessageService);
        // Run the check alarm handler to start monitoring the thread pool
        checkAlarmHandler.scheduleExecute();
    }
}
