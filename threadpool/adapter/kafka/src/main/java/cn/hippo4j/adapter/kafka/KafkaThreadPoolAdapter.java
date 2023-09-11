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

package cn.hippo4j.adapter.kafka;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cglib.core.Constants;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * Kafka thread-pool adapter.
 */
@Slf4j
public class KafkaThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Override
    public String mark() {
        return "Kafka";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(identify);

        if (listenerContainer == null) {
            log.warn("[{}] Kafka consuming thread pool not found.", identify);
            return result;
        }

        result.setThreadPoolKey(identify);
        if (listenerContainer instanceof ConcurrentMessageListenerContainer) {
            result.setCoreSize(((ConcurrentMessageListenerContainer<?, ?>) listenerContainer).getConcurrency());
            result.setMaximumSize(result.getCoreSize());
        } else {
            result.setCoreSize(1);
            result.setMaximumSize(1);
        }
        return result;
    }

    @Override
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> adapterStateList = new ArrayList<>();
        kafkaListenerEndpointRegistry.getListenerContainerIds().forEach(id -> adapterStateList.add(getThreadPoolState(id)));
        return adapterStateList;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(threadPoolKey);

        if (listenerContainer == null) {
            log.warn("[{}] Kafka consuming thread pool not found.", threadPoolKey);
            return false;
        }
        if (!(listenerContainer instanceof ConcurrentMessageListenerContainer)) {
            log.warn("[{}] Kafka consuming thread pool not support modify.", threadPoolKey);
            return false;
        }
        ConcurrentMessageListenerContainer concurrentContainer = (ConcurrentMessageListenerContainer) listenerContainer;
        int originalCoreSize = concurrentContainer.getConcurrency();
        int originalMaximumPoolSize = originalCoreSize;
        Integer concurrency = threadPoolAdapterParameter.getCorePoolSize();
        if (originalCoreSize < concurrency) {
            // add consumer
            if (!addConsumer(threadPoolKey, concurrentContainer, originalCoreSize, concurrency)) {
                return false;
            }
        } else {
            // stop consumer
            decreaseConsumer(threadPoolKey, concurrentContainer, originalCoreSize, concurrency);
        }
        concurrentContainer.setConcurrency(concurrency);
        log.info("[{}] Kafka consumption thread pool parameter change. coreSize: {}, maximumSize: {}",
                threadPoolKey,
                String.format(CHANGE_DELIMITER, originalCoreSize, concurrency),
                String.format(CHANGE_DELIMITER, originalMaximumPoolSize, concurrency));
        return true;

    }

    /**
     * @param threadPoolKey
     * @param concurrentContainer
     * @param originalCoreSize
     * @param concurrency
     * @since org.springframework.kafka.listener.ConcurrentMessageListenerContainer.doStop
     */
    private static void decreaseConsumer(String threadPoolKey, ConcurrentMessageListenerContainer concurrentContainer, int originalCoreSize, Integer concurrency) {
        int targetDecrease = originalCoreSize - concurrency;
        List<KafkaMessageListenerContainer> containers = (List) ReflectUtil.getFieldValue(concurrentContainer, "containers");
        Iterator<KafkaMessageListenerContainer> iterator = containers.iterator();
        int count = 0;
        while (iterator.hasNext() && count < targetDecrease) {
            KafkaMessageListenerContainer container = iterator.next();
            if (container.isRunning()) {
                container.stop(() -> {
                });
                count++;
            }
        }
        log.info("[{}] Kafka consumption change. target decrease {} ,real decrease {}", threadPoolKey, targetDecrease, count);
    }

    /**
     * @param threadPoolKey
     * @param concurrentContainer
     * @param originalCoreSize
     * @param concurrency
     * @return true success
     * @since org.springframework.kafka.listener.ConcurrentMessageListenerContainer#doStart()
     */
    @SneakyThrows
    private static boolean addConsumer(String threadPoolKey, ConcurrentMessageListenerContainer concurrentContainer, int originalCoreSize, Integer concurrency) {
        ContainerProperties containerProperties = concurrentContainer.getContainerProperties();
        TopicPartitionOffset[] topicPartitions = containerProperties.getTopicPartitions();
        if (topicPartitions != null && concurrency > topicPartitions.length) {
            log.warn("[{}] Kafka consuming thread pool not support modify. "
                    + "When specific partitions are provided, the concurrency must be less than or "
                    + "equal to the number of partitions;", threadPoolKey);
            return false;
        }
        List<KafkaMessageListenerContainer> containers = (List) ReflectUtil.getFieldValue(concurrentContainer, "containers");
        boolean alwaysClientIdSuffix = (Boolean) ReflectUtil.getFieldValue(concurrentContainer, "alwaysClientIdSuffix");
        int size = containers.size();
        for (int i = size; i < concurrency - originalCoreSize + size; i++) {
            KafkaMessageListenerContainer container = ReflectUtil.invoke(concurrentContainer, "constructContainer", containerProperties, topicPartitions, i);
            String beanName = concurrentContainer.getBeanName();
            container.setBeanName((beanName != null ? beanName : "consumer") + "-" + i);
            container.setApplicationContext(ApplicationContextHolder.getInstance());
            if (concurrentContainer.getApplicationEventPublisher() != null) {
                container.setApplicationEventPublisher(concurrentContainer.getApplicationEventPublisher());
            }
            container.setClientIdSuffix(concurrency > 1 || alwaysClientIdSuffix ? "-" + i : "");
            container.setGenericErrorHandler(ReflectUtil.invoke(concurrentContainer, "getGenericErrorHandler"));
            container.setAfterRollbackProcessor(ReflectUtil.invoke(concurrentContainer, "getAfterRollbackProcessor"));

            Method getRecordInterceptor = ReflectUtil.findDeclaredMethod(concurrentContainer.getClass(), "getRecordInterceptor", Constants.EMPTY_CLASS_ARRAY);
            ReflectUtil.setAccessible(getRecordInterceptor);
            container.setRecordInterceptor(ReflectUtil.invoke(concurrentContainer, getRecordInterceptor));

            Method isInterceptBeforeTx = ReflectUtil.findDeclaredMethod(concurrentContainer.getClass(), "isInterceptBeforeTx", Constants.EMPTY_CLASS_ARRAY);
            ReflectUtil.setAccessible(isInterceptBeforeTx);
            container.setInterceptBeforeTx(ReflectUtil.invoke(concurrentContainer, isInterceptBeforeTx));

            container.setEmergencyStop(() -> {
                concurrentContainer.stop(() -> {
                });
                ReflectUtil.invoke(concurrentContainer, "publishContainerStoppedEvent");
            });
            Method isPaused = ReflectUtil.findDeclaredMethod(concurrentContainer.getClass(), "isPaused", Constants.EMPTY_CLASS_ARRAY);
            ReflectUtil.setAccessible(isPaused);

            if (ReflectUtil.invoke(concurrentContainer, isPaused)) {
                container.pause();
            }
            container.start();
            containers.add(container);
        }
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry = ApplicationContextHolder.getBean(KafkaListenerEndpointRegistry.class);
            this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        } catch (Exception ex) {
            log.error("Failed to get Kafka thread pool.", ex);
        }
    }
}
