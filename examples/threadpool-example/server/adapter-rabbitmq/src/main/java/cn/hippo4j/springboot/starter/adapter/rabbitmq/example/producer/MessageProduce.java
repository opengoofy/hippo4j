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

package cn.hippo4j.springboot.starter.adapter.rabbitmq.example.producer;

import cn.hippo4j.common.toolkit.IdUtil;
import cn.hippo4j.example.core.dto.SendMessageDTO;
import cn.hippo4j.springboot.starter.adapter.rabbitmq.example.constants.SimpleMQConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Message produce.
 */
@Slf4j
@Component
@RestController
@AllArgsConstructor
public class MessageProduce {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/message/send")
    public String sendMessage(Integer count) {
        for (int i = 0; i < count; i++) {
            String keys = IdUtil.randomUUID();
            SendMessageDTO payload = SendMessageDTO.builder()
                    .receiver("156011xxx91")
                    .uid(keys)
                    .build();
            rabbitTemplate.convertAndSend(SimpleMQConstant.QUEUE_NAME, payload);
        }
        return "success";
    }
}
