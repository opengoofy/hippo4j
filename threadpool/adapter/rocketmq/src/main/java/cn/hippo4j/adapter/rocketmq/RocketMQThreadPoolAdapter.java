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

package cn.hippo4j.adapter.rocketmq;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.ConsumeMessageService;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * RocketMQ thread-pool adapter.
 */
@Slf4j
public class RocketMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private final Map<String, ThreadPoolExecutor> rocketmqConsumeExecutor = new HashMap<>();

    @Override
    public String mark() {
        return "RocketMQ";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        ThreadPoolExecutor rocketMQConsumeExecutor = rocketmqConsumeExecutor.get(identify);
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
        rocketmqConsumeExecutor.forEach(
                (key, val) -> adapterStateList.add(getThreadPoolState(key)));
        return adapterStateList;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        ThreadPoolExecutor rocketMQConsumeExecutor = rocketmqConsumeExecutor.get(threadPoolKey);
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
        Map<String, DefaultRocketMQListenerContainer> containerMap =
                ApplicationContextHolder.getBeansOfType(DefaultRocketMQListenerContainer.class);
        try {
            for (DefaultRocketMQListenerContainer container : containerMap.values()) {
                DefaultMQPushConsumer defaultMQPushConsumer = container.getConsumer();
                if (defaultMQPushConsumer != null) {
                    ConsumeMessageService consumeMessageService = defaultMQPushConsumer.getDefaultMQPushConsumerImpl().getConsumeMessageService();
                    ThreadPoolExecutor consumeExecutor = (ThreadPoolExecutor) ReflectUtil.getFieldValue(consumeMessageService, "consumeExecutor");
                    rocketmqConsumeExecutor.put(container.getConsumerGroup(), consumeExecutor);
                }
            }
        } catch (Exception ex) {
            log.error("Failed to get RocketMQ thread pool.", ex);
        }
    }
}
