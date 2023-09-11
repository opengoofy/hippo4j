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
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * RabbitMQ thread-pool adapter.
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private static final String RABBITMQ = "RabbitMQ";

    private static final String FILED_NAME = "executorService";

    private final Map<String, AbstractConnectionFactory> abstractConnectionFactoryMap;

    private final Map<String, ThreadPoolExecutor> rabbitmqThreadPoolTaskExecutor = new HashMap<>();

    @Override
    public String mark() {
        return RABBITMQ;
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState threadPoolAdapterState = new ThreadPoolAdapterState();
        ThreadPoolExecutor threadPoolTaskExecutor = rabbitmqThreadPoolTaskExecutor.get(identify);
        threadPoolAdapterState.setThreadPoolKey(identify);
        if (Objects.nonNull(threadPoolTaskExecutor)) {
            threadPoolAdapterState.setCoreSize(threadPoolTaskExecutor.getCorePoolSize());
            threadPoolAdapterState.setMaximumSize(threadPoolTaskExecutor.getMaximumPoolSize());
        }
        return threadPoolAdapterState;
    }

    @Override
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> adapterStateList = new ArrayList<>();
        rabbitmqThreadPoolTaskExecutor.forEach(
                (key, val) -> adapterStateList.add(getThreadPoolState(key)));
        return adapterStateList;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        ThreadPoolExecutor threadPoolTaskExecutor = rabbitmqThreadPoolTaskExecutor.get(threadPoolKey);
        if (Objects.nonNull(threadPoolTaskExecutor)) {
            int originalCoreSize = threadPoolTaskExecutor.getCorePoolSize();
            int originalMaximumPoolSize = threadPoolTaskExecutor.getMaximumPoolSize();
            ThreadPoolExecutorUtil.safeSetPoolSize(threadPoolTaskExecutor, threadPoolAdapterParameter.getCorePoolSize(), threadPoolAdapterParameter.getMaximumPoolSize());
            log.info("[{}] Rabbitmq consumption thread pool parameter change. coreSize: {}, maximumSize: {}",
                    threadPoolKey,
                    String.format(CHANGE_DELIMITER, originalCoreSize, threadPoolAdapterParameter.getCorePoolSize()),
                    String.format(CHANGE_DELIMITER, originalMaximumPoolSize, threadPoolAdapterParameter.getMaximumPoolSize()));
            return true;
        }
        log.warn("[{}] Rabbitmq consuming thread pool not found.", threadPoolKey);
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        abstractConnectionFactoryMap.forEach((beanName, abstractConnectionFactor) -> {
            ExecutorService executor = (ExecutorService) ReflectUtil.getFieldValue(abstractConnectionFactor, FILED_NAME);
            if (Objects.nonNull(executor)) {
                if (executor instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor threadPoolTaskExecutor = (ThreadPoolExecutor) executor;
                    rabbitmqThreadPoolTaskExecutor.put(beanName, threadPoolTaskExecutor);
                    log.info("Rabbitmq executor name {}", beanName);
                } else {
                    log.warn("Custom thread pools only support ThreadPoolExecutor");
                }
            }
        });
    }
}
