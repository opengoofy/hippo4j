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

import cn.hippo4j.message.api.NotifyConfigBuilder;
import cn.hippo4j.message.platform.DingSendMessageHandler;
import cn.hippo4j.message.platform.LarkSendMessageHandler;
import cn.hippo4j.message.platform.WeChatSendMessageHandler;
import cn.hippo4j.message.service.AlarmControlHandler;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import cn.hippo4j.message.service.Hippo4jSendMessageService;
import cn.hippo4j.message.service.SendMessageHandler;
import org.springframework.context.annotation.Bean;

/**
 * Message configuration.
 */
public class MessageConfiguration {

    @Bean
    public Hippo4jSendMessageService hippo4jSendMessageService(NotifyConfigBuilder serverNotifyConfigBuilder,
                                                               AlarmControlHandler alarmControlHandler) {
        return new Hippo4jBaseSendMessageService(serverNotifyConfigBuilder, alarmControlHandler);
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
