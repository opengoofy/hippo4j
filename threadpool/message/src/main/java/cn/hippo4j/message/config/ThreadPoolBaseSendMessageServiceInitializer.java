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

package cn.hippo4j.message.config;

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.message.api.NotifyConfigBuilder;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.service.SendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolBaseSendMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ThreadPoolBaseSendMessageServiceInitializer implements CommandLineRunner {

    private final NotifyConfigBuilder notifyConfigBuilder;
    private final ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService;

    @Override
    public void run(String... args) throws Exception {
        Map<String, SendMessageHandler> sendMessageHandlerMap =
                ApplicationContextHolder.getBeansOfType(SendMessageHandler.class);
        sendMessageHandlerMap.values().forEach(each -> threadPoolBaseSendMessageService.getSendMessageHandlers().put(each.getType(), each));
        Map<String, List<NotifyConfigDTO>> buildNotify = notifyConfigBuilder.buildNotify();
        threadPoolBaseSendMessageService.getNotifyConfigs().putAll(buildNotify);
    }
}
