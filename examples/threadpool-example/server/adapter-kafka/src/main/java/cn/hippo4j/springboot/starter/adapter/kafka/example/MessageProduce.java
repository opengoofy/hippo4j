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

package cn.hippo4j.springboot.starter.adapter.kafka.example;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Message produce.
 */
@Slf4j
@Component
@RestController
@AllArgsConstructor
public class MessageProduce {

    public static final String TOPIC = "test";

    private static final int SLEEP_TIME = 3;

    private final KafkaTemplate template;

    @GetMapping("/message/send")
    public String sendMessage() {
        template.send(TOPIC, "testMessage");
        return "success";
    }

    @PostConstruct
    public void init() {
        Thread t = new Thread(() -> {
            while (true) {
                String message = UUID.randomUUID().toString();
                template.send(TOPIC, "autoTestMessage " + message);
                try {
                    TimeUnit.SECONDS.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        });
        t.start();
    }
}
