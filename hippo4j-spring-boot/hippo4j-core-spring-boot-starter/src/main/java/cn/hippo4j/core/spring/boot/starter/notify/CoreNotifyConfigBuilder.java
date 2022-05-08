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

package cn.hippo4j.core.spring.boot.starter.notify;

import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.notify.AlarmControlHandler;
import cn.hippo4j.common.notify.NotifyConfigDTO;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.spring.boot.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.spring.boot.starter.config.ExecutorProperties;
import cn.hippo4j.core.spring.boot.starter.config.NotifyPlatformProperties;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Core notify config builder.
 *
 * @author chen.ma
 * @date 2022/2/25 00:24
 */
@AllArgsConstructor
public class CoreNotifyConfigBuilder implements NotifyConfigBuilder {

    private final AlarmControlHandler alarmControlHandler;

    private final BootstrapCoreProperties bootstrapCoreProperties;

    @Override
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        Map<String, List<NotifyConfigDTO>> resultMap = Maps.newHashMap();

        List<ExecutorProperties> executors = bootstrapCoreProperties.getExecutors();
        if (null != executors) {
            for (ExecutorProperties executor : executors) {
                resultMap.putAll(buildSingleNotifyConfig(executor));
            }
        }

        return resultMap;
    }

    /**
     * Build single notify config.
     *
     * @param executor
     * @return
     */
    public Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig(ExecutorProperties executor) {
        Map<String, List<NotifyConfigDTO>> resultMap = Maps.newHashMap();
        String threadPoolId = executor.getThreadPoolId();
        String alarmBuildKey = threadPoolId + "+ALARM";
        List<NotifyConfigDTO> alarmNotifyConfigs = Lists.newArrayList();

        List<NotifyPlatformProperties> notifyPlatforms = bootstrapCoreProperties.getNotifyPlatforms();
        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setTpId(threadPoolId);
            notifyConfig.setType("ALARM");
            notifyConfig.setSecret(platformProperties.getSecret());
            notifyConfig.setSecretKey(getToken(platformProperties));
            int interval = Optional.ofNullable(executor.getNotify())
                    .map(each -> each.getInterval())
                    .orElseGet(() -> bootstrapCoreProperties.getAlarmInterval() != null ? bootstrapCoreProperties.getAlarmInterval() : 5);
            notifyConfig.setInterval(interval);
            notifyConfig.setReceives(buildReceive(executor, platformProperties));
            alarmNotifyConfigs.add(notifyConfig);
        }
        resultMap.put(alarmBuildKey, alarmNotifyConfigs);

        String changeBuildKey = threadPoolId + "+CONFIG";
        List<NotifyConfigDTO> changeNotifyConfigs = Lists.newArrayList();

        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setTpId(threadPoolId);
            notifyConfig.setType("CONFIG");
            notifyConfig.setSecretKey(getToken(platformProperties));
            notifyConfig.setSecret(platformProperties.getSecret());
            notifyConfig.setReceives(buildReceive(executor, platformProperties));
            changeNotifyConfigs.add(notifyConfig);
        }
        resultMap.put(changeBuildKey, changeNotifyConfigs);

        resultMap.forEach(
                (key, val) -> val.stream()
                        .filter(each -> StrUtil.equals("ALARM", each.getType()))
                        .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval())));

        return resultMap;
    }

    private String buildReceive(ExecutorProperties executor, NotifyPlatformProperties platformProperties) {
        String receive;
        if (executor.getNotify() != null) {
            receive = executor.getNotify().getReceive();
            if (StrUtil.isBlank(receive)) {
                receive = bootstrapCoreProperties.getReceive();
                if (StrUtil.isBlank(receive)) {
                    Map<String, String> receives = executor.receives();
                    receive = receives.get(platformProperties.getPlatform());
                }
            }
        } else {
            receive = bootstrapCoreProperties.getReceive();
            if (StrUtil.isBlank(receive)) {
                Map<String, String> receives = executor.receives();
                receive = receives.get(platformProperties.getPlatform());
            }
        }

        return receive;
    }

    private String getToken(NotifyPlatformProperties platformProperties) {
        return StringUtil.isNotBlank(platformProperties.getToken()) ? platformProperties.getToken() : platformProperties.getSecretKey();
    }
}
