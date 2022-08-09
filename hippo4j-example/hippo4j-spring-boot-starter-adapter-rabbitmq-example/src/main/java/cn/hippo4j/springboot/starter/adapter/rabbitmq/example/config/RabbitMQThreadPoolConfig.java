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
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * RabbitMQ thread-pool config.
 */
@Configuration
public class RabbitMQThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor rabbitListenerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Specify the maximum number of threads.
        executor.setMaxPoolSize(5);
        // Specifies the minimum number of thread pool maintenance threads.
        executor.setCorePoolSize(5);
        // Specifies the number of tasks waiting to be processed.
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("RabbitListenerTaskExecutor-");
        return executor;
    }

    @Bean
    public AbstractRabbitListenerContainerFactory<?> defaultRabbitListenerContainerFactory(ThreadPoolTaskExecutor rabbitListenerTaskExecutor,
                                                                                           MessageConverter messageConverter, AbstractConnectionFactory abstractConnectionFactory) {
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        factory.setConnectionFactory(abstractConnectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConsumersPerQueue(10);
        abstractConnectionFactory.setExecutor(rabbitListenerTaskExecutor);
        return factory;
    }
}
