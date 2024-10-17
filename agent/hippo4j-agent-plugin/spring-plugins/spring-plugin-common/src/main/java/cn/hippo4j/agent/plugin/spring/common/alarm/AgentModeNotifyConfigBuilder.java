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

package cn.hippo4j.agent.plugin.spring.common.alarm;

import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.agent.plugin.spring.common.support.ThreadPoolCheckAlarmSupport;
import cn.hippo4j.common.api.IExecutorProperties;
import cn.hippo4j.common.model.executor.ExecutorNotifyProperties;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.NotifyPlatformProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.WebExecutorProperties;
import cn.hippo4j.threadpool.message.api.NotifyConfigBuilder;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES;
import static cn.hippo4j.common.constant.Constants.DEFAULT_INTERVAL;

/**
 * This class is responsible for building the notification configurations for thread pools in an agent mode.
 * It implements the {@link NotifyConfigBuilder} interface and provides methods to build and initialize
 * notification configurations for various platforms and types (e.g., ALARM, CONFIG).
 *
 * <p>The configuration is based on the properties loaded from the bootstrap configuration and includes
 * handling for alarm control and notification intervals.</p>
 *
 * TODO: This is copied from {@link cn.hippo4j.config.springboot.starter.notify.ConfigModeNotifyConfigBuilder} and can be refactored later
 */
@AllArgsConstructor
public class AgentModeNotifyConfigBuilder implements NotifyConfigBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolCheckAlarmSupport.class);

    private final AlarmControlHandler alarmControlHandler;

    private final WebThreadPoolService webThreadPoolService;

    /**
     * Builds the notification configurations for all executors defined in the bootstrap configuration.
     *
     * <p>This method filters the executors based on their alarm settings and constructs the notification
     * configurations accordingly. If global alarm settings are disabled and there are no specific alarms
     * configured for any executor, the method returns an empty map.</p>
     *
     * @return A map containing the notification configurations, keyed by the notification type (e.g., ALARM, CONFIG).
     */
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        boolean globalAlarm = Optional.ofNullable(BOOTSTRAP_CONFIG_PROPERTIES.getDefaultExecutor())
                .map(ExecutorProperties::getAlarm)
                .orElse(true);
        List<ExecutorProperties> executors = BOOTSTRAP_CONFIG_PROPERTIES.getExecutors();
        if (CollectionUtil.isEmpty(executors)) {
            LOGGER.warn("Failed to build notify, executors configuration is empty.");
            return resultMap;
        }
        List<ExecutorProperties> actual = executors.stream()
                .filter(each -> Optional.ofNullable(each.getAlarm())
                        .orElse(false))
                .collect(Collectors.toList());
        if (!globalAlarm && CollectionUtil.isEmpty(actual)) {
            return resultMap;
        }
        for (ExecutorProperties executorProperties : executors) {
            Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig = buildSingleNotifyConfig(executorProperties);
            initCacheAndLock(buildSingleNotifyConfig);
            resultMap.putAll(buildSingleNotifyConfig);
        }
        // register notify config for web
        WebExecutorProperties webProperties = BOOTSTRAP_CONFIG_PROPERTIES.getWeb();
        if (webProperties == null) {
            return resultMap;
        }
        if (StringUtil.isBlank(webProperties.getThreadPoolId())) {
            webProperties.setThreadPoolId(webThreadPoolService.getWebContainerType().getName());
        }
        Map<String, List<NotifyConfigDTO>> webSingleNotifyConfigMap = buildSingleNotifyConfig(webProperties);
        initCacheAndLock(webSingleNotifyConfigMap);
        resultMap.putAll(webSingleNotifyConfigMap);

        return resultMap;
    }

    /**
     * Builds the notification configurations for a single executor.
     *
     * <p>This method generates two types of notifications: ALARM and CONFIG. For each type, it creates
     * notification configurations based on the platforms defined in the bootstrap configuration.</p>
     *
     * @param executorProperties The properties of the executor for which to build the notification configurations.
     * @return A map containing the notification configurations for the given executor, keyed by the notification type.
     */
    public Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig(IExecutorProperties executorProperties) {
        String threadPoolId = executorProperties.getThreadPoolId();
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        String alarmBuildKey = threadPoolId + "+ALARM";
        List<NotifyConfigDTO> alarmNotifyConfigs = new ArrayList<>();
        List<NotifyPlatformProperties> notifyPlatforms = BOOTSTRAP_CONFIG_PROPERTIES.getNotifyPlatforms();
        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setTpId(threadPoolId);
            notifyConfig.setType("ALARM");
            notifyConfig.setSecret(platformProperties.getSecret());
            notifyConfig.setSecretKey(getToken(platformProperties));
            notifyConfig.setInterval(buildInterval(executorProperties));
            notifyConfig.setReceives(buildReceive(executorProperties));
            alarmNotifyConfigs.add(notifyConfig);
        }
        resultMap.put(alarmBuildKey, alarmNotifyConfigs);
        String changeBuildKey = threadPoolId + "+CONFIG";
        List<NotifyConfigDTO> changeNotifyConfigs = new ArrayList<>();
        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setTpId(threadPoolId);
            notifyConfig.setType("CONFIG");
            notifyConfig.setSecretKey(getToken(platformProperties));
            notifyConfig.setSecret(platformProperties.getSecret());
            notifyConfig.setReceives(buildReceive(executorProperties));
            changeNotifyConfigs.add(notifyConfig);
        }
        resultMap.put(changeBuildKey, changeNotifyConfigs);
        return resultMap;
    }

    /**
     * Retrieves the token for the given notification platform properties.
     *
     * <p>If the token is not explicitly set, the method returns the secret key as the fallback.</p>
     *
     * @param platformProperties The platform properties from which to retrieve the token.
     * @return The token or secret key associated with the given platform properties.
     */
    private String getToken(NotifyPlatformProperties platformProperties) {
        return StringUtil.isNotBlank(platformProperties.getToken()) ? platformProperties.getToken() : platformProperties.getSecretKey();
    }

    /**
     * Builds the notification interval for the given executor properties.
     *
     * <p>This method first checks the executor's specific notify configuration. If not set, it falls back
     * to the default executor configuration in the bootstrap properties.</p>
     *
     * @param executorProperties The properties of the executor for which to build the notification interval.
     * @return The notification interval in seconds.
     */
    private int buildInterval(IExecutorProperties executorProperties) {
        return Optional.ofNullable(executorProperties.getNotify())
                .map(ExecutorNotifyProperties::getInterval)
                .orElse(Optional.ofNullable(BOOTSTRAP_CONFIG_PROPERTIES.getDefaultExecutor())
                        .map(ExecutorProperties::getNotify)
                        .map(ExecutorNotifyProperties::getInterval)
                        .orElse(DEFAULT_INTERVAL));
    }

    /**
     * Builds the notification recipients for the given executor properties.
     *
     * <p>This method first checks the executor's specific notify configuration. If not set, it falls back
     * to the default executor configuration in the bootstrap properties.</p>
     *
     * @param executorProperties The properties of the executor for which to build the notification recipients.
     * @return A string containing the recipients of the notifications.
     */
    private String buildReceive(IExecutorProperties executorProperties) {
        return Optional.ofNullable(executorProperties.getNotify())
                .map(ExecutorNotifyProperties::getReceives)
                .orElse(Optional.ofNullable(BOOTSTRAP_CONFIG_PROPERTIES.getDefaultExecutor())
                        .map(ExecutorProperties::getNotify)
                        .map(ExecutorNotifyProperties::getReceives).orElse(""));
    }

    /**
     * Initializes the cache and lock mechanisms for the given notification configurations.
     *
     * <p>This method is primarily responsible for setting up alarm controls based on the notification
     * configurations, ensuring that the appropriate cache and lock mechanisms are initialized for
     * each thread pool and platform combination.</p>
     *
     * @param buildSingleNotifyConfig A map containing the notification configurations that need cache and lock initialization.
     */
    public void initCacheAndLock(Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig) {
        buildSingleNotifyConfig.forEach(
                (key, val) -> val.stream()
                        .filter(each -> Objects.equals("ALARM", each.getType()))
                        .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval())));
    }
}
