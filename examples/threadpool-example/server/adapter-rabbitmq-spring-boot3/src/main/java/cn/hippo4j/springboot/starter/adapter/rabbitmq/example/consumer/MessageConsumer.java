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

package cn.hippo4j.springboot.starter.adapter.rabbitmq.example.consumer;

import cn.hippo4j.example.core.dto.SendMessageDTO;
import cn.hippo4j.springboot.starter.adapter.rabbitmq.example.constants.SimpleMQConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Message consume.
 */
@Slf4j
@Component
public class MessageConsumer {

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue(SimpleMQConstant.QUEUE_NAME), containerFactory = "defaultRabbitListenerContainerFactory")
    public void receiveObject(SendMessageDTO simple) throws Exception {
        TimeUnit.SECONDS.sleep(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(simple);
        log.info("consumer1 threadId {} Message: {}", Thread.currentThread().getName(), message);
    }
}
