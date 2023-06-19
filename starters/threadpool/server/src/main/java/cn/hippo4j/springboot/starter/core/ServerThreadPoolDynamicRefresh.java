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

package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.common.api.ThreadPoolConfigChange;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.extension.enums.EnableEnum;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.threadpool.message.core.request.ChangeParameterNotifyRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_THREAD_POOL_TEXT;

/**
 * Server thread-pool dynamic refresh.
 */
@Slf4j
@AllArgsConstructor
public class ServerThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh {

    private final ThreadPoolConfigChange threadPoolConfigChange;

    @Override
    public void dynamicRefresh(String content) {
        ThreadPoolParameterInfo parameter = JSONUtil.parseObject(content, ThreadPoolParameterInfo.class);
        String threadPoolId = parameter.getTpId();
        ThreadPoolExecutor executor = ThreadPoolExecutorRegistry.getHolder(threadPoolId).getExecutor();
        refreshDynamicPool(parameter, executor);
    }

    private void refreshDynamicPool(ThreadPoolParameter parameter, ThreadPoolExecutor executor) {
        String threadPoolId = parameter.getTpId();
        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        String originalQuery = executor.getQueue().getClass().getSimpleName();
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.SECONDS);
        boolean originalAllowCoreThreadTimeOut = executor.allowsCoreThreadTimeOut();
        Long originalExecuteTimeOut = null;
        RejectedExecutionHandler rejectedExecutionHandler = executor.getRejectedExecutionHandler();
        if (executor instanceof DynamicThreadPoolExecutor) {
            DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
            rejectedExecutionHandler = dynamicExecutor.getRejectedExecutionHandler();
            originalExecuteTimeOut = dynamicExecutor.getExecuteTimeOut();
        }
        changePoolInfo(executor, parameter);
        ThreadPoolExecutor afterExecutor = ThreadPoolExecutorRegistry.getHolder(threadPoolId).getExecutor();
        String originalRejected = rejectedExecutionHandler.getClass().getSimpleName();
        Long executeTimeOut = Optional.ofNullable(parameter.getExecuteTimeOut()).orElse(0L);
        ChangeParameterNotifyRequest changeNotifyRequest = ChangeParameterNotifyRequest.builder()
                .beforeCorePoolSize(originalCoreSize)
                .beforeMaximumPoolSize(originalMaximumPoolSize)
                .beforeAllowsCoreThreadTimeOut(originalAllowCoreThreadTimeOut)
                .beforeKeepAliveTime(originalKeepAliveTime)
                .blockingQueueName(originalQuery)
                .beforeQueueCapacity(originalCapacity)
                .beforeRejectedName(originalRejected)
                .beforeExecuteTimeOut(originalExecuteTimeOut)
                .nowCorePoolSize(afterExecutor.getCorePoolSize())
                .nowMaximumPoolSize(afterExecutor.getMaximumPoolSize())
                .nowAllowsCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()))
                .nowKeepAliveTime(afterExecutor.getKeepAliveTime(TimeUnit.SECONDS))
                .nowQueueCapacity((afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size()))
                .nowRejectedName(RejectedPolicyTypeEnum.getRejectedNameByType(parameter.getRejectedType()))
                .nowExecuteTimeOut(executeTimeOut)
                .build();
        changeNotifyRequest.setThreadPoolId(threadPoolId);
        threadPoolConfigChange.sendPoolConfigChange(changeNotifyRequest);
        log.info(CHANGE_THREAD_POOL_TEXT,
                threadPoolId,
                String.format(CHANGE_DELIMITER, originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format(CHANGE_DELIMITER, originalCapacity, (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format(CHANGE_DELIMITER, originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.SECONDS)),
                String.format(CHANGE_DELIMITER, originalExecuteTimeOut, executeTimeOut),
                String.format(CHANGE_DELIMITER, originalRejected, RejectedPolicyTypeEnum.getRejectedNameByType(parameter.getRejectedType())),
                String.format(CHANGE_DELIMITER, originalAllowCoreThreadTimeOut, EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut())));
    }

    private void changePoolInfo(ThreadPoolExecutor executor, ThreadPoolParameter parameter) {
        if (parameter.getCoreSize() != null && parameter.getMaxSize() != null) {
            ThreadPoolExecutorUtil.safeSetPoolSize(executor, parameter.getCoreSize(), parameter.getMaxSize());
        } else {
            if (parameter.getMaxSize() != null) {
                executor.setMaximumPoolSize(parameter.getMaxSize());
            }
            if (parameter.getCoreSize() != null) {
                executor.setCorePoolSize(parameter.getCoreSize());
            }
        }
        if (parameter.getCapacity() != null
                && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getType(), parameter.getQueueType())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                ResizableCapacityLinkedBlockingQueue queue = (ResizableCapacityLinkedBlockingQueue) executor.getQueue();
                queue.setCapacity(parameter.getCapacity());
            } else {
                log.warn("The queue length cannot be modified. Queue type mismatch. Current queue type: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
        if (parameter.getKeepAliveTime() != null) {
            executor.setKeepAliveTime(parameter.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        Long executeTimeOut = Optional.ofNullable(parameter.getExecuteTimeOut()).orElse(0L);
        if (executor instanceof DynamicThreadPoolExecutor) {
            ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(executeTimeOut);
        }
        if (parameter.getRejectedType() != null) {
            RejectedExecutionHandler rejectedExecutionHandler = RejectedPolicyTypeEnum.createPolicy(parameter.getRejectedType());
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }
        if (parameter.getAllowCoreThreadTimeOut() != null) {
            executor.allowCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()));
        }
    }
}
