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

package cn.hippo4j.adapter.springcloud.stream.rabbitmq;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.DefaultBinding;
import org.springframework.cloud.stream.binding.InputBindingLifecycle;
import org.springframework.context.ApplicationListener;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * Spring cloud stream rabbit-mq thread-pool adapter.
 */
@Slf4j
public class SpringCloudStreamRabbitMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private final Map<String, AbstractMessageListenerContainer> rocketMqSpringCloudStreamConsumeExecutor = new HashMap<>();

    @Override
    public String mark() {
        return "RabbitMQSpringCloudStream";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        AbstractMessageListenerContainer messageListenerContainer = rocketMqSpringCloudStreamConsumeExecutor.get(identify);
        if (messageListenerContainer != null) {
            result.setThreadPoolKey(identify);
            if (messageListenerContainer instanceof SimpleMessageListenerContainer) {
                int concurrentConsumers = (int) ReflectUtil.getFieldValue(messageListenerContainer, "concurrentConsumers");
                result.setCoreSize(concurrentConsumers);
                Object maxConcurrentConsumers = ReflectUtil.getFieldValue(messageListenerContainer, "maxConcurrentConsumers");
                if (maxConcurrentConsumers != null) {
                    result.setMaximumSize((Integer) maxConcurrentConsumers);
                } else {
                    result.setMaximumSize(concurrentConsumers);
                }
            } else if (messageListenerContainer instanceof DirectMessageListenerContainer) {
                int consumersPerQueue = (int) ReflectUtil.getFieldValue(messageListenerContainer, "consumersPerQueue");
                result.setCoreSize(consumersPerQueue);
                result.setMaximumSize(consumersPerQueue);
            }
            return result;
        }
        log.warn("[{}] RabbitMQ consuming thread pool not found.", identify);
        return result;
    }

    @Override
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> adapterStateList = new ArrayList<>();
        rocketMqSpringCloudStreamConsumeExecutor.forEach(
                (key, val) -> adapterStateList.add(getThreadPoolState(key)));
        return adapterStateList;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        AbstractMessageListenerContainer messageListenerContainer = rocketMqSpringCloudStreamConsumeExecutor.get(threadPoolKey);
        if (messageListenerContainer != null) {
            synchronized (rocketMqSpringCloudStreamConsumeExecutor) {
                Integer corePoolSize = threadPoolAdapterParameter.getCorePoolSize();
                Integer maximumPoolSize = threadPoolAdapterParameter.getMaximumPoolSize();
                if (messageListenerContainer instanceof SimpleMessageListenerContainer) {
                    int originalCoreSize = (int) ReflectUtil.getFieldValue(messageListenerContainer, "concurrentConsumers");
                    Object maxConcurrentConsumers = ReflectUtil.getFieldValue(messageListenerContainer, "maxConcurrentConsumers");
                    int originalMaximumPoolSize;
                    if (maxConcurrentConsumers != null) {
                        originalMaximumPoolSize = (Integer) maxConcurrentConsumers;
                    } else {
                        originalMaximumPoolSize = originalCoreSize;
                    }
                    SimpleMessageListenerContainer simpleMessageListenerContainer = (SimpleMessageListenerContainer) messageListenerContainer;
                    if (originalCoreSize > maximumPoolSize) {
                        simpleMessageListenerContainer.setConcurrentConsumers(corePoolSize);
                        simpleMessageListenerContainer.setMaxConcurrentConsumers(maximumPoolSize);
                    } else {
                        simpleMessageListenerContainer.setMaxConcurrentConsumers(maximumPoolSize);
                        simpleMessageListenerContainer.setConcurrentConsumers(corePoolSize);
                    }
                    log.info("[{}] RabbitMQ consumption thread pool parameter change. coreSize: {}, maximumSize: {}",
                            threadPoolKey,
                            String.format(CHANGE_DELIMITER, originalCoreSize, corePoolSize),
                            String.format(CHANGE_DELIMITER, originalMaximumPoolSize, maximumPoolSize));
                } else if (messageListenerContainer instanceof DirectMessageListenerContainer) {
                    int originalCoreSize = (int) ReflectUtil.getFieldValue(messageListenerContainer, "consumersPerQueue");
                    DirectMessageListenerContainer directMessageListenerContainer = (DirectMessageListenerContainer) messageListenerContainer;
                    directMessageListenerContainer.setConsumersPerQueue(maximumPoolSize);
                    log.info("[{}] RabbitMQ consumption thread pool parameter change. coreSize: {}",
                            threadPoolKey,
                            String.format(CHANGE_DELIMITER, originalCoreSize, corePoolSize));
                } else {
                    log.warn("[{}] RabbitMQ consuming thread pool not support. messageListenerContainer: {}", threadPoolKey, messageListenerContainer.getClass());
                    return false;
                }
            }
            return true;
        }
        log.warn("[{}] RabbitMQ consuming thread pool not found.", threadPoolKey);
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        InputBindingLifecycle bindingLifecycle = ApplicationContextHolder.getBean(InputBindingLifecycle.class);
        Collection<Binding<Object>> inputBindings = Optional.ofNullable(ReflectUtil.getFieldValue(bindingLifecycle, "inputBindings"))
                .map(each -> (Collection<Binding<Object>>) each).orElse(null);
        if (CollectionUtil.isEmpty(inputBindings)) {
            log.info("InputBindings record not found.");
            return;
        }
        try {
            for (Binding<Object> each : inputBindings) {
                String bindingName = each.getBindingName();
                DefaultBinding defaultBinding = (DefaultBinding) each;
                Object lifecycle = ReflectUtil.getFieldValue(defaultBinding, "lifecycle");
                if (lifecycle instanceof AmqpInboundChannelAdapter) {
                    AbstractMessageListenerContainer rabbitMQListenerContainer = (AbstractMessageListenerContainer) ReflectUtil.getFieldValue(lifecycle, "messageListenerContainer");
                    rocketMqSpringCloudStreamConsumeExecutor.put(bindingName, rabbitMQListenerContainer);
                }
            }
        } catch (Exception ex) {
            log.error("Failed to get input-bindings thread pool.", ex);
        }
    }
}
