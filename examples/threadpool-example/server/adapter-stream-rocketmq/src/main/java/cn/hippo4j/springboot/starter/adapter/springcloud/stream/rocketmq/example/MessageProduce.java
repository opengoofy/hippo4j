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

package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rocketmq.example;

import cn.hippo4j.common.toolkit.IdUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.example.core.dto.SendMessageDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Message produce.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class MessageProduce {

    private final MessageChannel output;

    public static final String MESSAGE_CENTER_SEND_MESSAGE_TAG = "framework_message-center_send-message_tag";

    public static final String MESSAGE_CENTER_SAVE_MESSAGE_TAG = "framework_message-center_save-message_tag";

    private static final int MAX_SEND_SIZE = 10;

    private static final long SEND_TIMEOUT = 2000L;

    @GetMapping("/message/send")
    public String sendMessage() {
        for (int i = 0; i < MAX_SEND_SIZE; i++) {
            sendMessage(MESSAGE_CENTER_SEND_MESSAGE_TAG);
            sendMessage(MESSAGE_CENTER_SAVE_MESSAGE_TAG);
        }
        return "success";
    }

    private void sendMessage(String tags) {
        String keys = IdUtil.randomUUID();
        SendMessageDTO payload = SendMessageDTO.builder()
                .receiver("156011xxx91")
                .uid(keys)
                .build();
        Message<?> message = MessageBuilder
                .withPayload(JSONUtil.toJSONString(payload))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, tags)
                .build();
        long startTime = System.currentTimeMillis();
        boolean sendResult = false;
        try {
            sendResult = output.send(message, SEND_TIMEOUT);
        } finally {
            log.info("Send status: {}, Keys: {}, Execute time: {} ms, Message: {}",
                    sendResult,
                    keys,
                    System.currentTimeMillis() - startTime,
                    JSONUtil.toJSONString(payload));
        }
    }
}
