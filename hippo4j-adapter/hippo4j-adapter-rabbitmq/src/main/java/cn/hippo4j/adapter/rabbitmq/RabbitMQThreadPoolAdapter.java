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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * RabbitMQ thread-pool adapter.
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMQThreadPoolAdapter implements ThreadPoolAdapter, SmartInitializingSingleton {

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
        ThreadPoolAdapterState threadPoolAdapterState = new ThreadPoolAdapterState();
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = RABBITMQ_EXECUTOR.get(identify);
        ThreadPoolTaskExecutor threadPoolTaskExecutor = RABBITMQ_THREAD_POOL_TASK_EXECUTOR.get(identify);
        threadPoolAdapterState.setThreadPoolKey(identify);
        if (Objects.nonNull(simpleAsyncTaskExecutor)) {
            threadPoolAdapterState.setCoreSize(simpleAsyncTaskExecutor.getConcurrencyLimit());
        }
        if (Objects.nonNull(threadPoolTaskExecutor)) {
            threadPoolAdapterState.setCoreSize(threadPoolTaskExecutor.getCorePoolSize());
            threadPoolAdapterState.setMaximumSize(threadPoolTaskExecutor.getMaxPoolSize());
        }
        return threadPoolAdapterState;
    }
    
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> adapterStateList = Lists.newArrayList();
        RABBITMQ_EXECUTOR.forEach(
                (key, val) -> adapterStateList.add(getThreadPoolState(key)));
        RABBITMQ_THREAD_POOL_TASK_EXECUTOR.forEach(
                (key, val) -> adapterStateList.add(getThreadPoolState(key)));
        return adapterStateList;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = RABBITMQ_EXECUTOR.get(threadPoolKey);
        ThreadPoolTaskExecutor threadPoolTaskExecutor = RABBITMQ_THREAD_POOL_TASK_EXECUTOR.get(threadPoolKey);
        if (Objects.nonNull(threadPoolTaskExecutor)) {
            int originalCoreSize = threadPoolTaskExecutor.getCorePoolSize();
            int originalMaximumPoolSize = threadPoolTaskExecutor.getMaxPoolSize();
            threadPoolTaskExecutor.setMaxPoolSize(threadPoolAdapterParameter.getMaximumPoolSize());
            threadPoolTaskExecutor.setCorePoolSize(threadPoolAdapterParameter.getCorePoolSize());
            log.info("[{}] rabbitmq consumption thread pool parameter change. coreSize :: {}, maximumSize :: {}",
                    threadPoolKey,
                    String.format(CHANGE_DELIMITER, originalCoreSize, threadPoolAdapterParameter.getCorePoolSize()),
                    String.format(CHANGE_DELIMITER, originalMaximumPoolSize, threadPoolAdapterParameter.getMaximumPoolSize()));
            return true;
        }
        if (Objects.nonNull(simpleAsyncTaskExecutor)) {
            int concurrencyLimit = simpleAsyncTaskExecutor.getConcurrencyLimit();
            simpleAsyncTaskExecutor.setConcurrencyLimit(threadPoolAdapterParameter.getCorePoolSize());
            log.info("[{}] rabbitmq consumption thread pool parameter change. coreSize :: {}",
                    threadPoolKey,
                    String.format(CHANGE_DELIMITER, concurrencyLimit, threadPoolAdapterParameter.getCorePoolSize()));
            return true;
        }
        log.warn("[{}] rabbitmq consuming thread pool not found.", threadPoolKey);
        return false;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (AbstractRabbitListenerContainerFactory<?> consumerWorkService : abstractRabbitListenerContainerFactories) {
            // 是否为自定义线程池
            Executor executor = (Executor) ReflectUtil.getFieldValue(consumerWorkService, FiledName);
            if (Objects.isNull(executor)) {
                // 获取默认线程池
                // 优先获取用户配置的
                AbstractMessageListenerContainer listenerContainer1 = consumerWorkService.createListenerContainer();
                SimpleAsyncTaskExecutor fieldValue = (SimpleAsyncTaskExecutor) ReflectUtil.getFieldValue(listenerContainer1, FiledName);
                log.info("rabbitmq executor name {}", FiledName);
                RABBITMQ_EXECUTOR.put(FiledName, fieldValue);
            } else {
                if (executor instanceof ThreadPoolTaskExecutor) {
                    ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;
                    String beanName = (String) ReflectUtil.getFieldValue(threadPoolTaskExecutor, BEAN_NAME_FILED);
                    RABBITMQ_THREAD_POOL_TASK_EXECUTOR.put(beanName, threadPoolTaskExecutor);
                    log.info("rabbitmq executor name {}", beanName);
                } else {
                    log.warn("Custom thread pools only support ThreadPoolTaskExecutor");
                }
            }
        }
    }
}
