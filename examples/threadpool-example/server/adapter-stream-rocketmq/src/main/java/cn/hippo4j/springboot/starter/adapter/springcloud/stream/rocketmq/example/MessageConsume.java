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

import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.example.core.dto.SendMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Message consume.
 */
@Slf4j
@Component
public class MessageConsume {

    @StreamListener(MySink.INPUT)
    public void consumeMessage(@Payload SendMessageDTO message, @Headers Map headers) {
        long startTime = System.currentTimeMillis();
        try {
            // ignore
            log.info("Message: {}", JSONUtil.toJSONString(message));
        } finally {
            log.info("Keys: {}, Msg id: {}, Execute time: {} ms, Message: {}", headers.get("rocketmq_KEYS"), headers.get("rocketmq_MESSAGE_ID"), System.currentTimeMillis() - startTime,
                    JSONUtil.toJSONString(message));
        }
        log.info("Input current thread name: {}", Thread.currentThread().getName());
    }

    @StreamListener(MySink.INPUT2)
    public void consumeSaveMessage(@Payload SendMessageDTO message, @Headers Map headers) {
        long startTime = System.currentTimeMillis();
        try {
            // ignore
            log.info("Message: {}", JSONUtil.toJSONString(message));
        } finally {
            log.info("Keys: {}, Msg id: {}, Execute time: {} ms, Message: {}", headers.get("rocketmq_KEYS"), headers.get("rocketmq_MESSAGE_ID"), System.currentTimeMillis() - startTime,
                    JSONUtil.toJSONString(message));
        }
        log.info("Input2 current thread name: {}", Thread.currentThread().getName());
    }
}
