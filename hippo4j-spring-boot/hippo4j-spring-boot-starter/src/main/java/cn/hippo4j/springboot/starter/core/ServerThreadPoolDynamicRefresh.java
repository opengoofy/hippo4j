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

import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.AbstractDynamicExecutorSupport;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.RejectedTypeEnum;
import cn.hippo4j.core.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_THREAD_POOL_TEXT;

/**
 * Thread pool dynamic refresh.
 *
 * @author chen.ma
 * @date 2021/6/20 15:51
 */
@Slf4j
@AllArgsConstructor
public class ServerThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh {

    private final ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler;

    @Override
    public void dynamicRefresh(String content) {
        ThreadPoolParameterInfo parameter = JSONUtil.parseObject(content, ThreadPoolParameterInfo.class);

        String threadPoolId = parameter.getTpId();
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        refreshDynamicPool(parameter, executor);
    }

    /**
     * Refresh dynamic pool.
     *
     * @param parameter
     * @param executor
     */
    public void refreshDynamicPool(ThreadPoolParameter parameter, ThreadPoolExecutor executor) {
        String threadPoolId = parameter.getTpId();
        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        String originalQuery = executor.getQueue().getClass().getSimpleName();
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.SECONDS);
        boolean originalAllowCoreThreadTimeOut = executor.allowsCoreThreadTimeOut();

        Long originalExecuteTimeOut = null;
        RejectedExecutionHandler rejectedExecutionHandler = executor.getRejectedExecutionHandler();
        if (executor instanceof AbstractDynamicExecutorSupport) {
            DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
            rejectedExecutionHandler = dynamicExecutor.getRedundancyHandler();
            originalExecuteTimeOut = dynamicExecutor.getExecuteTimeOut();
        }
        String originalRejected = rejectedExecutionHandler.getClass().getSimpleName();
        // Send change message.
        ChangeParameterNotifyRequest request = new ChangeParameterNotifyRequest();
        request.setBeforeCorePoolSize(originalCoreSize);
        request.setBeforeMaximumPoolSize(originalMaximumPoolSize);
        request.setBeforeAllowsCoreThreadTimeOut(originalAllowCoreThreadTimeOut);
        request.setBeforeKeepAliveTime(originalKeepAliveTime);
        request.setBlockingQueueName(originalQuery);
        request.setBeforeQueueCapacity(originalCapacity);
        request.setBeforeRejectedName(originalRejected);
        request.setBeforeExecuteTimeOut(originalExecuteTimeOut);
        request.setThreadPoolId(threadPoolId);

        changePoolInfo(executor, parameter);
        ThreadPoolExecutor afterExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();
        request.setNowCorePoolSize(afterExecutor.getCorePoolSize());
        request.setNowMaximumPoolSize(afterExecutor.getMaximumPoolSize());
        request.setNowAllowsCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()));
        request.setNowKeepAliveTime(afterExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        request.setNowQueueCapacity((afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size()));
        request.setNowRejectedName(RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType()));
        request.setNowExecuteTimeOut(originalExecuteTimeOut);
        threadPoolNotifyAlarmHandler.sendPoolConfigChange(request);
        log.info(CHANGE_THREAD_POOL_TEXT,
                threadPoolId.toUpperCase(),
                String.format(CHANGE_DELIMITER, originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format(CHANGE_DELIMITER, originalQuery, QueueTypeEnum.getBlockingQueueNameByType(parameter.getQueueType())),
                String.format(CHANGE_DELIMITER, originalCapacity,
                        (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format(CHANGE_DELIMITER, originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.SECONDS)),
                String.format(CHANGE_DELIMITER, originalExecuteTimeOut, originalExecuteTimeOut),
                String.format(CHANGE_DELIMITER, originalRejected, RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType())),
                String.format(CHANGE_DELIMITER, originalAllowCoreThreadTimeOut, EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut())));
    }

    /**
     * Change pool info.
     *
     * @param executor
     * @param parameter
     */
    public void changePoolInfo(ThreadPoolExecutor executor, ThreadPoolParameter parameter) {
        if (parameter.getCoreSize() != null && parameter.getMaxSize() != null) {
            if (parameter.getMaxSize() < executor.getMaximumPoolSize()) {
                executor.setCorePoolSize(parameter.getCoreSize());
                executor.setMaximumPoolSize(parameter.getMaxSize());
            } else {
                executor.setMaximumPoolSize(parameter.getMaxSize());
                executor.setCorePoolSize(parameter.getCoreSize());
            }
        } else {
            if (parameter.getMaxSize() != null) {
                executor.setMaximumPoolSize(parameter.getMaxSize());
            }
            if (parameter.getCoreSize() != null) {
                executor.setCorePoolSize(parameter.getCoreSize());
            }
        }
        if (parameter.getCapacity() != null
                && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.type, parameter.getQueueType())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                ResizableCapacityLinkedBlockingQueue queue = (ResizableCapacityLinkedBlockingQueue) executor.getQueue();
                queue.setCapacity(parameter.getCapacity());
            } else {
                log.warn("The queue length cannot be modified. Queue type mismatch. Current queue type :: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
        if (parameter.getKeepAliveTime() != null) {
            executor.setKeepAliveTime(parameter.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        if (parameter.getRejectedType() != null) {
            RejectedExecutionHandler rejectedExecutionHandler = RejectedTypeEnum.createPolicy(parameter.getRejectedType());
            if (executor instanceof AbstractDynamicExecutorSupport) {
                DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
                dynamicExecutor.setRedundancyHandler(rejectedExecutionHandler);
                AtomicLong rejectCount = dynamicExecutor.getRejectCount();
                rejectedExecutionHandler = RejectedProxyUtil.createProxy(rejectedExecutionHandler, parameter.getTpId(), rejectCount);
            }
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }
        if (parameter.getAllowCoreThreadTimeOut() != null) {
            executor.allowCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()));
        }
    }
}
