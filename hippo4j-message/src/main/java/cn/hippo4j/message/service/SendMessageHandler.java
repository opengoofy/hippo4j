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

package cn.hippo4j.message.service;

import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.common.api.NotifyRequest;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.message.request.WebChangeParameterNotifyRequest;

/**
 * Send message handler.
 */
public interface SendMessageHandler {

    /**
     * Get the message send type.
     *
     * @return message type
     */
    String getType();

    /**
     * Send alarm message.
     *
     * @param notifyConfig       notify config
     * @param alarmNotifyRequest alarm notify request
     */
    void sendAlarmMessage(NotifyConfigDTO notifyConfig, AlarmNotifyRequest alarmNotifyRequest);

    /**
     * Send change message.
     *
     * @param notifyConfig                 notify config
     * @param changeParameterNotifyRequest change parameter notify request
     */
    void sendChangeMessage(NotifyConfigDTO notifyConfig, ChangeParameterNotifyRequest changeParameterNotifyRequest);

    /**
     * Send web change message.
     *
     * @param notifyConfig                 notify config
     * @param changeParameterNotifyRequest parameter notify request
     */
    default void sendWebChangeMessage(NotifyConfigDTO notifyConfig, WebChangeParameterNotifyRequest changeParameterNotifyRequest) throws IllegalAccessException {
        throw new IllegalAccessException("Please implement this method before using it.");
    }
}
