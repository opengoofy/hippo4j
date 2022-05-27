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

package cn.hippo4j.springboot.starter.adapter.rabbitmq.example.config;

import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : wh
 * @date : 2022/5/24 10:02
 * @description:
 */
@Configuration
public class RabbitMQThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor rabbitListenerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 指定线程的最大数量
        executor.setMaxPoolSize(10);
        // 指定线程池维护线程的最少数量
        executor.setCorePoolSize(10);
        // 指定等待处理的任务数
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("RabbitListenerTaskExecutor-");
        return executor;
    }

    @Bean
    public AbstractRabbitListenerContainerFactory<?> defaultRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                                           ThreadPoolTaskExecutor rabbitListenerTaskExecutor,
                                                                                           ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(2);
        factory.setTaskExecutor(rabbitListenerTaskExecutor);
        return factory;
    }
}
