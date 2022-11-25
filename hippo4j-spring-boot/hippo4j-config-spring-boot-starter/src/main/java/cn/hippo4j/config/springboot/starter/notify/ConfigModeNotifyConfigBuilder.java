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

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.config.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.config.springboot.starter.config.NotifyPlatformProperties;
import cn.hippo4j.message.api.NotifyConfigBuilder;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.service.AlarmControlHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Config mode notify config builder.
 */
@AllArgsConstructor
@Slf4j
public class ConfigModeNotifyConfigBuilder implements NotifyConfigBuilder {

    private final AlarmControlHandler alarmControlHandler;

    private final BootstrapConfigProperties configProperties;

    @Override
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        boolean globalAlarm = Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getAlarm()).orElse(true);
        List<ExecutorProperties> executors = configProperties.getExecutors();
        if (CollectionUtil.isEmpty(executors)) {
            log.warn("Failed to build notify, executors configuration is empty.");
            return resultMap;
        }
        List<ExecutorProperties> actual = executors.stream().filter(each -> Optional.ofNullable(each.getAlarm()).orElse(false)).collect(Collectors.toList());
        if (!globalAlarm && CollectionUtil.isEmpty(actual)) {
            return resultMap;
        }
        for (ExecutorProperties executorProperties : executors) {
            Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig = buildSingleNotifyConfig(executorProperties);
            initCacheAndLock(buildSingleNotifyConfig);
            resultMap.putAll(buildSingleNotifyConfig);
        }
        return resultMap;
    }

    /**
     * Build single notify config.
     *
     * @param executorProperties
     * @return
     */
    public Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig(ExecutorProperties executorProperties) {
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        String threadPoolId = executorProperties.getThreadPoolId();
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
            int interval = Optional.ofNullable(executorProperties.getNotify())
                    .map(each -> each.getInterval())
                    .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getNotify()).map(each -> each.getInterval()).orElse(5));
            notifyConfig.setInterval(interval);
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

    private String buildReceive(ExecutorProperties executorProperties) {
        String receives = Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getNotify()).map(each -> each.getReceives()).orElse("");
        if (executorProperties.getNotify() != null && StringUtil.isNotEmpty(executorProperties.getNotify().getReceives())) {
            receives = executorProperties.getNotify().getReceives();
        }
        return receives;
    }

    private String getToken(NotifyPlatformProperties platformProperties) {
        return StringUtil.isNotBlank(platformProperties.getToken()) ? platformProperties.getToken() : platformProperties.getSecretKey();
    }
}
