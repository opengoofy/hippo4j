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

package cn.hippo4j.threadpool.dynamic.mode.config.refresher.event;

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.extension.design.Observer;
import cn.hippo4j.common.extension.design.ObserverMessage;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_THREAD_POOL_TEXT;

/**
 * Dynamic thread-pool refresh listener.
 */
@RequiredArgsConstructor
public class DynamicThreadPoolRefreshListener implements Observer<BootstrapConfigProperties> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicThreadPoolRefreshListener.class);

    @Override
    public void accept(ObserverMessage<BootstrapConfigProperties> observerMessage) {
        BootstrapConfigProperties bindableConfigProperties = observerMessage.message();
        List<ExecutorProperties> executors = bindableConfigProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            // Check whether the thread pool configuration is empty and whether the parameters have changed
            ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
            if (executorHolder.isEmpty() || !checkPropertiesConsistency(executorHolder, properties)) {
                continue;
            }
            dynamicRefreshThreadPool(executorHolder, properties);
            sendChangeNotificationMessage(executorHolder, properties);
            executorHolder.setExecutorProperties(properties);
        }
    }

    /**
     * Check consistency.
     *
     * @param executorHolder executor holder
     * @param properties     properties after dynamic thread pool change
     */
    private boolean checkPropertiesConsistency(ThreadPoolExecutorHolder executorHolder, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = executorHolder.getExecutorProperties();
        ThreadPoolExecutor executor = executorHolder.getExecutor();
        boolean result = (properties.getCorePoolSize() != null && !Objects.equals(beforeProperties.getCorePoolSize(), properties.getCorePoolSize()))
                || (properties.getMaximumPoolSize() != null && !Objects.equals(beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()))
                || (properties.getAllowCoreThreadTimeOut() != null && !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut()))
                || (properties.getExecuteTimeOut() != null && !Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut()))
                || (properties.getKeepAliveTime() != null && !Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()))
                || (properties.getRejectedHandler() != null && !Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler()))
                ||
                ((properties.getQueueCapacity() != null && !Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                        && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName(), executor.getQueue().getClass().getSimpleName())));
        return result;
    }

    /**
     * Dynamic refresh thread-pool.
     *
     * @param executorHolder executor holder
     * @param properties     properties after dynamic thread pool change
     */
    private void dynamicRefreshThreadPool(ThreadPoolExecutorHolder executorHolder, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = executorHolder.getExecutorProperties();
        ThreadPoolExecutor executor = executorHolder.getExecutor();
        if (properties.getMaximumPoolSize() != null && properties.getCorePoolSize() != null) {
            ThreadPoolExecutorUtil.safeSetPoolSize(executor, properties.getCorePoolSize(), properties.getMaximumPoolSize());
        } else {
            if (properties.getMaximumPoolSize() != null) {
                executor.setMaximumPoolSize(properties.getMaximumPoolSize());
            }
            if (properties.getCorePoolSize() != null) {
                executor.setCorePoolSize(properties.getCorePoolSize());
            }
        }
        if (properties.getAllowCoreThreadTimeOut() != null && !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())) {
            executor.allowCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
        }
        // TODO
        if (properties.getExecuteTimeOut() != null && !Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut())) {
            // if (executor instanceof DynamicThreadPoolExecutor) {
            // ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(properties.getExecuteTimeOut());
            // }
        }
        if (properties.getRejectedHandler() != null && !Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler())) {
            RejectedExecutionHandler rejectedExecutionHandler = RejectedPolicyTypeEnum.createPolicy(properties.getRejectedHandler());
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }
        if (properties.getKeepAliveTime() != null && !Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime())) {
            executor.setKeepAliveTime(properties.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        if (properties.getQueueCapacity() != null && !Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName(), executor.getQueue().getClass().getSimpleName())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                ResizableCapacityLinkedBlockingQueue<?> queue = (ResizableCapacityLinkedBlockingQueue<?>) executor.getQueue();
                queue.setCapacity(properties.getQueueCapacity());
            } else {
                LOGGER.warn("The queue length cannot be modified. Queue type mismatch. Current queue type: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
    }

    private void sendChangeNotificationMessage(ThreadPoolExecutorHolder executorHolder, ExecutorProperties properties) {
        ExecutorProperties executorProperties = executorHolder.getExecutorProperties();
        // TODO log cannot be printed
        LOGGER.info(CHANGE_THREAD_POOL_TEXT,
                executorHolder.getThreadPoolId(),
                String.format(CHANGE_DELIMITER, executorProperties.getCorePoolSize(), properties.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, executorProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()),
                String.format(CHANGE_DELIMITER, executorProperties.getQueueCapacity(), properties.getQueueCapacity()),
                String.format(CHANGE_DELIMITER, executorProperties.getKeepAliveTime(), properties.getKeepAliveTime()),
                String.format(CHANGE_DELIMITER, executorProperties.getRejectedHandler(), properties.getRejectedHandler()),
                String.format(CHANGE_DELIMITER, executorProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut()));
    }
}
