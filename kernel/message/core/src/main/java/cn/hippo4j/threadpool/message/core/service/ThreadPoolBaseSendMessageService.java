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

package cn.hippo4j.threadpool.message.core.service;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.threadpool.message.api.AlarmControlDTO;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.api.NotifyTypeEnum;
import cn.hippo4j.threadpool.message.core.request.AlarmNotifyRequest;
import cn.hippo4j.threadpool.message.core.request.ChangeParameterNotifyRequest;
import cn.hippo4j.threadpool.message.core.request.WebChangeParameterNotifyRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hippo-4j base send message service.
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolBaseSendMessageService implements ThreadPoolSendMessageService {

    private final AlarmControlHandler alarmControlHandler;

    @Getter
    private final Map<String, List<NotifyConfigDTO>> notifyConfigs = new HashMap<>();

    @Getter
    private final Map<String, SendMessageHandler> sendMessageHandlers = new HashMap<>();

    @Override
    public void sendAlarmMessage(NotifyTypeEnum typeEnum, AlarmNotifyRequest alarmNotifyRequest) {
        String threadPoolId = alarmNotifyRequest.getThreadPoolId();
        String buildKey = generateAlarmKey(threadPoolId);
        List<NotifyConfigDTO> notifyList = notifyConfigs.get(buildKey);
        if (CollectionUtil.isEmpty(notifyList)) {
            return;
        }
        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("[{}] Please configure alarm notification on the server.", threadPoolId);
                    return;
                }
                if (isSendAlarm(each.getTpId(), each.getPlatform(), typeEnum)) {
                    alarmNotifyRequest.setNotifyTypeEnum(typeEnum);
                    messageHandler.sendAlarmMessage(each, alarmNotifyRequest);
                }
            } catch (Exception ex) {
                log.warn("Failed to send thread pool alarm notification. key: [{}]", threadPoolId, ex);
            }
        });
    }

    @Override
    public void sendChangeMessage(ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();
        String buildKey = generateConfigKey(threadPoolId);
        List<NotifyConfigDTO> notifyList = notifyConfigs.get(buildKey);
        if (CollectionUtil.isEmpty(notifyList)) {
            log.warn("[{}] Please configure alarm notification on the server.", threadPoolId);
            return;
        }
        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("[{}] Please configure alarm notification on the server.", threadPoolId);
                    return;
                }
                messageHandler.sendChangeMessage(each, changeParameterNotifyRequest);
            } catch (Exception ex) {
                log.error("Failed to send thread pool change notification. key: [{}]", threadPoolId, ex);
            }
        });
    }

    @Override
    public void sendChangeMessage(WebChangeParameterNotifyRequest webChangeParameterNotifyRequest) {
        String threadPoolId = webChangeParameterNotifyRequest.getThreadPoolId();
        String buildKey = generateConfigKey(threadPoolId);
        List<NotifyConfigDTO> notifyList = notifyConfigs.get(buildKey);
        if (CollectionUtil.isEmpty(notifyList)) {
            log.warn("[{}] Please configure alarm notification on the server.", threadPoolId);
            return;
        }
        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("[{}] Please configure alarm notification on the server.", threadPoolId);
                    return;
                }
                messageHandler.sendWebChangeMessage(each, webChangeParameterNotifyRequest);
            } catch (IllegalAccessException ex) {
                log.warn("Failed to send thread pool change notification. key: [{}]", threadPoolId);
            } catch (Exception ex) {
                log.error("Failed to send thread pool change notification. key: [{}]", threadPoolId, ex);
            }
        });
    }

    private String generateConfigKey(String threadPoolId) {
        return new StringBuilder()
                .append(threadPoolId)
                .append("+")
                .append("CONFIG")
                .toString();
    }

    private String generateAlarmKey(String threadPoolId) {
        return new StringBuilder()
                .append(threadPoolId)
                .append("+")
                .append("ALARM")
                .toString();
    }

    /**
     * Is send alarm.
     *
     * @param threadPoolId thread-pool id
     * @param platform     platform
     * @param typeEnum     type enum
     * @return
     */
    private boolean isSendAlarm(String threadPoolId, String platform, NotifyTypeEnum typeEnum) {
        AlarmControlDTO alarmControl = AlarmControlDTO.builder()
                .threadPool(threadPoolId)
                .platform(platform)
                .typeEnum(typeEnum)
                .build();
        return alarmControlHandler.isSendAlarm(alarmControl);
    }

    /**
     * Put platform.
     *
     * @param notifyConfigs notify configs
     */
    public synchronized void putPlatform(Map<String, List<NotifyConfigDTO>> notifyConfigs) {
        this.notifyConfigs.putAll(notifyConfigs);
    }
}
