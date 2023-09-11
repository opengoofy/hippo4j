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

import cn.hippo4j.threadpool.message.core.request.AlarmNotifyRequest;
import cn.hippo4j.threadpool.message.core.request.ChangeParameterNotifyRequest;
import cn.hippo4j.threadpool.message.core.request.WebChangeParameterNotifyRequest;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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

    @SneakyThrows
    default String readUtf8String(String path) {
        int endFlagCode = -1;
        String resultReadStr;
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (
                InputStream inputStream = classPathResource.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            int result = bis.read();
            while (result != endFlagCode) {
                buf.write((byte) result);
                result = bis.read();
            }
            resultReadStr = buf.toString("UTF-8");
        }
        return resultReadStr;
    }
}
