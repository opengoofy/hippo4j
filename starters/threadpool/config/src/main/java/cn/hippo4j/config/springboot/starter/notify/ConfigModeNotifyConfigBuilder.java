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

package cn.hippo4j.config.springboot.starter.notify;

import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.common.api.IExecutorProperties;
import cn.hippo4j.common.model.executor.ExecutorNotifyProperties;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.NotifyPlatformProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.WebExecutorProperties;
import cn.hippo4j.threadpool.message.api.NotifyConfigBuilder;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Config mode notify config builder.
 */
@AllArgsConstructor
@Slf4j
public class ConfigModeNotifyConfigBuilder implements NotifyConfigBuilder {

    private final AlarmControlHandler alarmControlHandler;

    private final BootstrapConfigProperties configProperties;

    private final WebThreadPoolService webThreadPoolService;

    private static final int DEFAULT_INTERVAL = 5;

    @Override
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        boolean globalAlarm = Optional.ofNullable(configProperties.getDefaultExecutor())
                .map(ExecutorProperties::getAlarm)
                .orElse(true);
        List<ExecutorProperties> executors = configProperties.getExecutors();
        if (CollectionUtil.isEmpty(executors)) {
            log.warn("Failed to build notify, executors configuration is empty.");
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
        WebExecutorProperties webProperties = configProperties.getWeb();
        if (webProperties == null) {
            return resultMap;
        }
        if (StringUtil.isBlank(webProperties.getThreadPoolId())) {
            webProperties.setThreadPoolId(webThreadPoolService.getWebContainerType().getName());
        }
        Map<String, List<NotifyConfigDTO>> webSingleNotifyConfigMap =
                buildSingleNotifyConfig(webProperties);
        initCacheAndLock(webSingleNotifyConfigMap);
        resultMap.putAll(webSingleNotifyConfigMap);

        return resultMap;
    }

    /**
     * Build single notify config.
     *
     * @param executorProperties
     * @return
     */
    public Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig(IExecutorProperties executorProperties) {
        String threadPoolId = executorProperties.getThreadPoolId();
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        String alarmBuildKey = threadPoolId + "+ALARM";
        List<NotifyConfigDTO> alarmNotifyConfigs = new ArrayList<>();
        List<NotifyPlatformProperties> notifyPlatforms = configProperties.getNotifyPlatforms();
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

    public void initCacheAndLock(Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig) {
        buildSingleNotifyConfig.forEach(
                (key, val) -> val.stream()
                        .filter(each -> Objects.equals("ALARM", each.getType()))
                        .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval())));
    }

    private int buildInterval(IExecutorProperties executorProperties) {
        return Optional.ofNullable(executorProperties.getNotify())
                .map(ExecutorNotifyProperties::getInterval)
                .orElse(Optional.ofNullable(configProperties.getDefaultExecutor())
                        .map(ExecutorProperties::getNotify)
                        .map(ExecutorNotifyProperties::getInterval)
                        .orElse(DEFAULT_INTERVAL));
    }

    private String buildReceive(IExecutorProperties executorProperties) {
        return Optional.ofNullable(executorProperties.getNotify())
                .map(ExecutorNotifyProperties::getReceives)
                .orElse(Optional.ofNullable(configProperties.getDefaultExecutor())
                        .map(ExecutorProperties::getNotify)
                        .map(ExecutorNotifyProperties::getReceives).orElse(""));
    }

    private String getToken(NotifyPlatformProperties platformProperties) {
        return StringUtil.isNotBlank(platformProperties.getToken()) ? platformProperties.getToken() : platformProperties.getSecretKey();
    }
}
