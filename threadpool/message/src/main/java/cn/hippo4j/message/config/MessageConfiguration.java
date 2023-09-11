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

import cn.hippo4j.threadpool.message.api.NotifyConfigBuilder;
import cn.hippo4j.threadpool.message.core.platform.DingSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.LarkSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.WeChatSendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import cn.hippo4j.threadpool.message.core.service.SendMessageHandler;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolBaseSendMessageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Message configuration.
 */
@ConditionalOnProperty(prefix = "spring.dynamic.thread-pool", value = "enable", matchIfMissing = true, havingValue = "true")
public class MessageConfiguration {

    @Bean
    public ThreadPoolBaseSendMessageService threadPoolSendMessageService(AlarmControlHandler alarmControlHandler) {
        return new ThreadPoolBaseSendMessageService(alarmControlHandler);
    }

    @Bean
    public ThreadPoolBaseSendMessageServiceInitializer threadPoolBaseSendMessageServiceInitializer(NotifyConfigBuilder notifyConfigBuilder,
                                                                                                   ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService) {
        return new ThreadPoolBaseSendMessageServiceInitializer(notifyConfigBuilder, threadPoolBaseSendMessageService);
    }

    @Bean
    public AlarmControlHandler alarmControlHandler() {
        return new AlarmControlHandler();
    }

    @Bean
    public SendMessageHandler dingSendMessageHandler() {
        return new DingSendMessageHandler();
    }

    @Bean
    public SendMessageHandler larkSendMessageHandler() {
        return new LarkSendMessageHandler();
    }

    @Bean
    public SendMessageHandler weChatSendMessageHandler() {
        return new WeChatSendMessageHandler();
    }
}
