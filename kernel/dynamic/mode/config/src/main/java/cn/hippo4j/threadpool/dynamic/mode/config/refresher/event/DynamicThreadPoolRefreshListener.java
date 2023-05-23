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

import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.extension.design.Observer;
import cn.hippo4j.common.extension.design.ObserverMessage;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.threadpool.dynamic.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.core.executor.manage.GlobalConfigThreadPoolManage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RequiredArgsConstructor
public class DynamicThreadPoolRefreshListener implements Observer<BootstrapConfigProperties> {

    @Override
    public void accept(ObserverMessage<BootstrapConfigProperties> observerMessage) {
        BootstrapConfigProperties bindableConfigProperties = observerMessage.message();
        List<ExecutorProperties> executors = bindableConfigProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            dynamicRefreshPool(threadPoolId, properties);
            ExecutorProperties beforeProperties = GlobalConfigThreadPoolManage.getProperties(properties.getThreadPoolId());
            log.info(CHANGE_THREAD_POOL_TEXT,
                    threadPoolId,
                    String.format(CHANGE_DELIMITER, beforeProperties.getCorePoolSize(), properties.getCorePoolSize()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), properties.getQueueCapacity()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getRejectedHandler(), properties.getRejectedHandler()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut()));
        }
    }

    /**
     * Dynamic refresh pool.
     *
     * @param threadPoolId
     * @param properties
     */
    private void dynamicRefreshPool(String threadPoolId, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = GlobalConfigThreadPoolManage.getProperties(properties.getThreadPoolId());
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId);
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
                log.warn("The queue length cannot be modified. Queue type mismatch. Current queue type: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
    }
}
