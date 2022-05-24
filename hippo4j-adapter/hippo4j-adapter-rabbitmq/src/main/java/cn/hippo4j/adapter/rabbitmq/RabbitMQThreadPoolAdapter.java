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

package cn.hippo4j.adapter.rabbitmq;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.ReflectUtil;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * RabbitMQ thread-pool adapter.
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private static final String RABBITMQ = "RabbitMQ";

    private static final String FiledName = "taskExecutor";

    private static final String BEAN_NAME_FILED = "beanName";

    private final List<AbstractRabbitListenerContainerFactory<?>> abstractRabbitListenerContainerFactories;

    private final Map<String, SimpleAsyncTaskExecutor> RABBITMQ_EXECUTOR = Maps.newHashMap();

    private final Map<String, ThreadPoolTaskExecutor> RABBITMQ_THREAD_POOL_TASK_EXECUTOR = Maps.newHashMap();

    @Override
    public String mark() {
        return RABBITMQ;
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        return null;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        for (AbstractRabbitListenerContainerFactory<?> consumerWorkService : abstractRabbitListenerContainerFactories) {
            // 是否为自定义线程池
            Executor executor = (Executor) ReflectUtil.getFieldValue(consumerWorkService, FiledName);
            if (Objects.isNull(executor)) {
                // 获取默认线程池
                // 优先获取用户配置的
                AbstractMessageListenerContainer listenerContainer1 = consumerWorkService.createListenerContainer();
                SimpleAsyncTaskExecutor fieldValue = (SimpleAsyncTaskExecutor) ReflectUtil.getFieldValue(listenerContainer1, FiledName);
                RABBITMQ_EXECUTOR.put(FiledName, fieldValue);
            } else {
                if (executor instanceof ThreadPoolTaskExecutor) {
                    ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;
                    String beanName = (String) ReflectUtil.getFieldValue(threadPoolTaskExecutor, BEAN_NAME_FILED);
                    RABBITMQ_THREAD_POOL_TASK_EXECUTOR.put(beanName, threadPoolTaskExecutor);
                } else {
                    log.warn("Custom thread pools only support ThreadPoolTaskExecutor");
                } 
            } 
        }

    }
}
