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

package cn.hippo4j.core.springboot.starter.refresher.event;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.AbstractDynamicExecutorSupport;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.RejectedTypeEnum;
import cn.hippo4j.core.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import cn.hippo4j.core.springboot.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.core.springboot.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.core.springboot.starter.support.GlobalCoreThreadPoolManage;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_THREAD_POOL_TEXT;
import static cn.hippo4j.core.springboot.starter.refresher.event.Hippo4jCoreDynamicRefreshEventOrder.EXECUTORS_LISTENER;

/**
 * Executors listener.
 */
@Slf4j
@RequiredArgsConstructor
@Order(EXECUTORS_LISTENER)
public class ExecutorsListener implements ApplicationListener<Hippo4jCoreDynamicRefreshEvent> {

    private final ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler;

    private final CoreNotifyConfigBuilder coreNotifyConfigBuilder;

    private final Hippo4jBaseSendMessageService hippo4jBaseSendMessageService;

    @Override
    public void onApplicationEvent(Hippo4jCoreDynamicRefreshEvent threadPoolDynamicRefreshEvent) {
        BootstrapCoreProperties bindableCoreProperties = threadPoolDynamicRefreshEvent.getBootstrapCoreProperties();
        List<ExecutorProperties> executors = bindableCoreProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            // Check whether the notification configuration is consistent.
            // this operation will not trigger the notification.
            checkNotifyConsistencyAndReplace(properties);
            if (!checkConsistency(threadPoolId, properties)) {
                continue;
            }
            // refresh executor pool.
            dynamicRefreshPool(threadPoolId, properties);
            // old properties.
            ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
            // refresh executor properties.
            GlobalCoreThreadPoolManage.refresh(threadPoolId, properties);
            log.info(CHANGE_THREAD_POOL_TEXT,
                    threadPoolId.toUpperCase(),
                    String.format(CHANGE_DELIMITER, beforeProperties.getCorePoolSize(), properties.getCorePoolSize()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getBlockingQueue(), properties.getBlockingQueue()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), properties.getQueueCapacity()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getRejectedHandler(), properties.getRejectedHandler()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut()));
            try {
                threadPoolNotifyAlarmHandler.sendPoolConfigChange(newChangeRequest(beforeProperties, properties));
            } catch (Throwable ex) {
                log.error("Failed to send changSmartApplicationListenere notice. Message :: {}", ex.getMessage());
            }
        }
    }

    /**
     * Construct ChangeParameterNotifyRequest instance
     *
     * @param beforeProperties old properties
     * @param properties       new properties
     * @return instance
     */
    private ChangeParameterNotifyRequest newChangeRequest(ExecutorProperties beforeProperties, ExecutorProperties properties) {
        ChangeParameterNotifyRequest changeRequest = new ChangeParameterNotifyRequest();
        changeRequest.setBeforeCorePoolSize(beforeProperties.getCorePoolSize());
        changeRequest.setBeforeMaximumPoolSize(beforeProperties.getMaximumPoolSize());
        changeRequest.setBeforeAllowsCoreThreadTimeOut(beforeProperties.getAllowCoreThreadTimeOut());
        changeRequest.setBeforeKeepAliveTime(beforeProperties.getKeepAliveTime());
        changeRequest.setBlockingQueueName(beforeProperties.getBlockingQueue());
        changeRequest.setBeforeQueueCapacity(beforeProperties.getQueueCapacity());
        changeRequest.setBeforeRejectedName(beforeProperties.getRejectedHandler());
        changeRequest.setBeforeExecuteTimeOut(beforeProperties.getExecuteTimeOut());
        changeRequest.setThreadPoolId(beforeProperties.getThreadPoolId());
        changeRequest.setNowCorePoolSize(properties.getCorePoolSize());
        changeRequest.setNowMaximumPoolSize(properties.getMaximumPoolSize());
        changeRequest.setNowAllowsCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
        changeRequest.setNowKeepAliveTime(properties.getKeepAliveTime());
        changeRequest.setNowQueueCapacity(properties.getQueueCapacity());
        changeRequest.setNowRejectedName(properties.getRejectedHandler());
        changeRequest.setNowExecuteTimeOut(properties.getExecuteTimeOut());
        return changeRequest;
    }

    /**
     * Check notify consistency and replace.
     *
     * @param properties
     */
    private void checkNotifyConsistencyAndReplace(ExecutorProperties properties) {
        boolean checkNotifyConfig = false;
        boolean checkNotifyAlarm = false;
        List<String> changeKeys = Lists.newArrayList();
        Map<String, List<NotifyConfigDTO>> newDynamicThreadPoolNotifyMap = coreNotifyConfigBuilder.buildSingleNotifyConfig(properties);
        Map<String, List<NotifyConfigDTO>> notifyConfigs = hippo4jBaseSendMessageService.getNotifyConfigs();
        if (CollectionUtil.isNotEmpty(notifyConfigs)) {
            for (Map.Entry<String, List<NotifyConfigDTO>> each : newDynamicThreadPoolNotifyMap.entrySet()) {
                if (checkNotifyConfig) {
                    break;
                }
                List<NotifyConfigDTO> notifyConfigDTOS = notifyConfigs.get(each.getKey());
                for (NotifyConfigDTO notifyConfig : each.getValue()) {
                    if (!notifyConfigDTOS.contains(notifyConfig)) {
                        checkNotifyConfig = true;
                        changeKeys.add(each.getKey());
                        break;
                    }
                }
            }
        }
        if (checkNotifyConfig) {
            coreNotifyConfigBuilder.initCacheAndLock(newDynamicThreadPoolNotifyMap);
            hippo4jBaseSendMessageService.putPlatform(newDynamicThreadPoolNotifyMap);
        }
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(properties.getThreadPoolId());
        if (threadPoolNotifyAlarm != null && properties.getNotify() != null) {
            ThreadPoolNotifyAlarm notify = properties.getNotify();
            boolean isAlarm = notify.getIsAlarm();
            Integer activeAlarm = notify.getActiveAlarm();
            Integer capacityAlarm = notify.getCapacityAlarm();
            if (threadPoolNotifyAlarm.getIsAlarm() != isAlarm
                    || threadPoolNotifyAlarm.getActiveAlarm() != activeAlarm
                    || threadPoolNotifyAlarm.getCapacityAlarm() != capacityAlarm) {
                checkNotifyAlarm = true;
                threadPoolNotifyAlarm.setIsAlarm(isAlarm);
                threadPoolNotifyAlarm.setActiveAlarm(activeAlarm);
                threadPoolNotifyAlarm.setCapacityAlarm(capacityAlarm);
            }
        }
        if (checkNotifyConfig || checkNotifyAlarm) {
            log.info("[{}] Dynamic thread pool notification property changes.", properties.getThreadPoolId());
        }
    }

    /**
     * Check consistency.
     *
     * @param threadPoolId
     * @param properties
     */
    private boolean checkConsistency(String threadPoolId, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutor(threadPoolId);
        if (executor == null) {
            return false;
        }
        boolean result = !Objects.equals(beforeProperties.getCorePoolSize(), properties.getCorePoolSize())
                || !Objects.equals(beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize())
                || !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())
                || !Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut())
                || !Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime())
                || !Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler())
                ||
                (!Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                        && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.name, executor.getQueue().getClass().getSimpleName()));
        return result;
    }

    /**
     * Dynamic refresh pool.
     *
     * @param threadPoolId
     * @param properties
     */
    private void dynamicRefreshPool(String threadPoolId, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();
        if (properties.getMaximumPoolSize() != null && properties.getCorePoolSize() != null) {
            if (properties.getMaximumPoolSize() < executor.getMaximumPoolSize()) {
                executor.setCorePoolSize(properties.getCorePoolSize());
                executor.setMaximumPoolSize(properties.getMaximumPoolSize());
            } else {
                executor.setMaximumPoolSize(properties.getMaximumPoolSize());
                executor.setCorePoolSize(properties.getCorePoolSize());
            }
        } else {
            if (properties.getMaximumPoolSize() != null) {
                executor.setMaximumPoolSize(properties.getMaximumPoolSize());
            }
            if (properties.getCorePoolSize() != null) {
                executor.setCorePoolSize(properties.getCorePoolSize());
            }
        }
        if (!Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())) {
            executor.allowCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
        }
        if (!Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut())) {
            if (executor instanceof AbstractDynamicExecutorSupport) {
                ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(properties.getExecuteTimeOut());
            }
        }
        if (!Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler())) {
            RejectedExecutionHandler rejectedExecutionHandler = RejectedTypeEnum.createPolicy(properties.getRejectedHandler());
            if (executor instanceof AbstractDynamicExecutorSupport) {
                DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
                dynamicExecutor.setRedundancyHandler(rejectedExecutionHandler);
                AtomicLong rejectCount = dynamicExecutor.getRejectCount();
                rejectedExecutionHandler = RejectedProxyUtil.createProxy(rejectedExecutionHandler, threadPoolId, rejectCount);
            }
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }
        if (!Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime())) {
            executor.setKeepAliveTime(properties.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        if (!Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.name, executor.getQueue().getClass().getSimpleName())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                ResizableCapacityLinkedBlockingQueue<?> queue = (ResizableCapacityLinkedBlockingQueue<?>) executor.getQueue();
                queue.setCapacity(properties.getQueueCapacity());
            } else {
                log.warn("The queue length cannot be modified. Queue type mismatch. Current queue type :: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
    }
}
