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
import cn.hippo4j.common.propertie.EnvironmentProperties;
import cn.hippo4j.threadpool.alarm.handler.DefaultThreadPoolCheckAlarmHandler;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.platform.DingSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.LarkSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.WeChatSendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import cn.hippo4j.threadpool.message.core.service.SendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolBaseSendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService = createThreadPoolBaseSendMessageService(alarmControlHandler);

            // Initialize the alarm platform information
            initializeSendMessageHandlers(threadPoolBaseSendMessageService, alarmControlHandler);

            // Initialize the thread pool check alarm handler with necessary services
            DefaultThreadPoolCheckAlarmHandler checkAlarmHandler = new DefaultThreadPoolCheckAlarmHandler(threadPoolBaseSendMessageService);

            // Run the check alarm handler to start monitoring the thread pool
            checkAlarmHandler.scheduleExecute();
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
     * @param alarmControlHandler The {@link AlarmControlHandler} used to handle alarms and notifications.
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
        AgentModeNotifyConfigBuilder notifyConfigBuilder = new AgentModeNotifyConfigBuilder(alarmControlHandler);
        Map<String, List<NotifyConfigDTO>> notifyConfigs = notifyConfigBuilder.buildNotify();
        threadPoolBaseSendMessageService.getNotifyConfigs().putAll(notifyConfigs);
    }
}
