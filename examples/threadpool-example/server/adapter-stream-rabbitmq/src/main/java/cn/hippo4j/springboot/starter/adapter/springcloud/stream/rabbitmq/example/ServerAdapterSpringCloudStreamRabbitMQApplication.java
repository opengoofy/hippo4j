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

package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rabbitmq.example;

import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Consumer;

/**
 * Server Adapter Spring Cloud Stream RabbitMQ Application
 */
@Slf4j
@EnableDynamicThreadPool
@SpringBootApplication
public class ServerAdapterSpringCloudStreamRabbitMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerAdapterSpringCloudStreamRabbitMQApplication.class, args);
    }

    @Bean
    public Consumer<Message<String>> demoInput() {
        return message -> {
            MessageHeaders headers = message.getHeaders();
            log.info("Input current thread name: {} ,{} received from partition {}",
                    Thread.currentThread().getName(),
                    JSONUtil.toJSONString(message.getPayload()),
                    headers.get(AmqpHeaders.CONSUMER_QUEUE));
        };
    }

}
