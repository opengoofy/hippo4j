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

package cn.hippo4j.adapter.springcloud.stream.rocketmq;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.core.config.ApplicationContextHolder;
import com.alibaba.cloud.stream.binder.rocketmq.consuming.RocketMQListenerBindingContainer;
import com.alibaba.cloud.stream.binder.rocketmq.integration.RocketMQInboundChannelAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.DefaultBinding;
import org.springframework.cloud.stream.binding.InputBindingLifecycle;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * Spring cloud stream rocketMQ thread-pool adapter.
 */
@Slf4j
public class SpringCloudStreamRocketMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private final Map<String, ThreadPoolExecutor> rocketMqSpringCloudStreamConsumeExecutor = new HashMap<>();

    @Override
    public String mark() {
        return "RocketMQSpringCloudStream";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        ThreadPoolExecutor rocketMQConsumeExecutor = rocketMqSpringCloudStreamConsumeExecutor.get(identify);
        if (rocketMQConsumeExecutor != null) {
            result.setThreadPoolKey(identify);
            result.setCoreSize(rocketMQConsumeExecutor.getCorePoolSize());
            result.setMaximumSize(rocketMQConsumeExecutor.getMaximumPoolSize());
            return result;
        }
        log.warn("[{}] RocketMQ consuming thread pool not found.", identify);
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
        ThreadPoolExecutor rocketMQConsumeExecutor = rocketMqSpringCloudStreamConsumeExecutor.get(threadPoolKey);
        if (rocketMQConsumeExecutor != null) {
            int originalCoreSize = rocketMQConsumeExecutor.getCorePoolSize();
            int originalMaximumPoolSize = rocketMQConsumeExecutor.getMaximumPoolSize();
            ThreadPoolExecutorUtil.safeSetPoolSize(rocketMQConsumeExecutor, threadPoolAdapterParameter.getCorePoolSize(), threadPoolAdapterParameter.getMaximumPoolSize());
            log.info("[{}] RocketMQ consumption thread pool parameter change. coreSize: {}, maximumSize: {}",
                    threadPoolKey,
                    String.format(CHANGE_DELIMITER, originalCoreSize, rocketMQConsumeExecutor.getCorePoolSize()),
                    String.format(CHANGE_DELIMITER, originalMaximumPoolSize, rocketMQConsumeExecutor.getMaximumPoolSize()));
            return true;
        }
        log.warn("[{}] RocketMQ consuming thread pool not found.", threadPoolKey);
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        InputBindingLifecycle bindingLifecycle = ApplicationContextHolder.getBean(InputBindingLifecycle.class);
        Collection<Binding<Object>> inputBindings = Optional.ofNullable(ReflectUtil.getFieldValue(bindingLifecycle, "inputBindings")).map(each -> (Collection<Binding<Object>>) each).orElse(null);
        if (CollectionUtil.isEmpty(inputBindings)) {
            log.info("InputBindings record not found.");
        }
        try {
            for (Binding<Object> each : inputBindings) {
                String bindingName = each.getBindingName();
                DefaultBinding defaultBinding = (DefaultBinding) each;
                RocketMQInboundChannelAdapter lifecycle = (RocketMQInboundChannelAdapter) ReflectUtil.getFieldValue(defaultBinding, "lifecycle");
                RocketMQListenerBindingContainer rocketMQListenerContainer = (RocketMQListenerBindingContainer) ReflectUtil.getFieldValue(lifecycle, "rocketMQListenerContainer");
                DefaultMQPushConsumer consumer = rocketMQListenerContainer.getConsumer();
                DefaultMQPushConsumerImpl defaultMQPushConsumerImpl = consumer.getDefaultMQPushConsumerImpl();
                ConsumeMessageConcurrentlyService consumeMessageService = (ConsumeMessageConcurrentlyService) defaultMQPushConsumerImpl.getConsumeMessageService();
                ThreadPoolExecutor consumeExecutor = (ThreadPoolExecutor) ReflectUtil.getFieldValue(consumeMessageService, "consumeExecutor");
                rocketMqSpringCloudStreamConsumeExecutor.put(bindingName, consumeExecutor);
            }
        } catch (Exception ex) {
            log.error("Failed to get input-bindings thread pool.", ex);
        }
    }
}
