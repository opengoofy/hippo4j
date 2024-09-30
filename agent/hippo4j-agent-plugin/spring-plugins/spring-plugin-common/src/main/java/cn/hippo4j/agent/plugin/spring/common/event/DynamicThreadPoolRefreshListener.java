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

package cn.hippo4j.agent.plugin.spring.common.event;

import cn.hippo4j.agent.core.util.CollectionUtil;
import cn.hippo4j.agent.plugin.spring.common.alarm.AgentModeNotifyConfigBuilder;
import cn.hippo4j.agent.plugin.spring.common.support.ThreadPoolCheckAlarmSupport;
import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.extension.design.Observer;
import cn.hippo4j.common.extension.design.ObserverMessage;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.request.ChangeParameterNotifyRequest;
import cn.hippo4j.threadpool.message.core.service.GlobalNotifyAlarmManage;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolBaseSendMessageService;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolNotifyAlarm;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    private static final ILog LOG = LogManager.getLogger(DynamicThreadPoolRefreshListener.class);

    @Override
    public void accept(ObserverMessage<BootstrapConfigProperties> observerMessage) {
        BootstrapConfigProperties bindableConfigProperties = observerMessage.message();
        List<ExecutorProperties> executors = bindableConfigProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            // Check whether the thread pool configuration is empty and whether the parameters have changed
            ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
            /*
             * Check whether the notification configuration is consistent, this operation will not trigger the notification.
             */
            checkNotifyConsistencyAndReplace(properties);
            if (executorHolder.isEmpty() || !checkPropertiesConsistency(executorHolder, properties)) {
                continue;
            }
            dynamicRefreshThreadPool(executorHolder, properties);
            sendChangeNotificationMessage(executorHolder, properties);
            executorHolder.setExecutorProperties(properties);
        }
    }

    /**
     * Check notify consistency and replace.
     *
     * @param executorProperties
     */
    private void checkNotifyConsistencyAndReplace(ExecutorProperties executorProperties) {
        boolean checkNotifyConfig = false;
        boolean checkNotifyAlarm = false;
        ThreadPoolBaseSendMessageService threadPoolBaseSendMessageService = ThreadPoolCheckAlarmSupport.getThreadPoolBaseSendMessageService();
        AgentModeNotifyConfigBuilder agentNotifyConfigBuilder = ThreadPoolCheckAlarmSupport.getAgentNotifyConfigBuilder();
        // Build a new notification configuration for the Agent
        Map<String, List<NotifyConfigDTO>> newDynamicThreadPoolNotifyMap = agentNotifyConfigBuilder.buildSingleNotifyConfig(executorProperties);
        Map<String, List<NotifyConfigDTO>> notifyConfigs = threadPoolBaseSendMessageService.getNotifyConfigs();
        if (CollectionUtil.isNotEmpty(notifyConfigs)) {
            for (Map.Entry<String, List<NotifyConfigDTO>> each : newDynamicThreadPoolNotifyMap.entrySet()) {
                if (checkNotifyConfig) {
                    break;
                }
                List<NotifyConfigDTO> notifyConfigDTOS = notifyConfigs.get(each.getKey());
                for (NotifyConfigDTO notifyConfig : each.getValue()) {
                    if (!notifyConfigDTOS.contains(notifyConfig)) {
                        checkNotifyConfig = true;
                        break;
                    }
                }
            }
        }
        if (checkNotifyConfig) {
            agentNotifyConfigBuilder.initCacheAndLock(newDynamicThreadPoolNotifyMap);
            threadPoolBaseSendMessageService.putPlatform(newDynamicThreadPoolNotifyMap);
        }
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(executorProperties.getThreadPoolId());
        if (threadPoolNotifyAlarm != null) {
            Boolean isAlarm = executorProperties.getAlarm();
            Integer activeAlarm = executorProperties.getActiveAlarm();
            Integer capacityAlarm =
                    executorProperties.getCapacityAlarm();
            if ((isAlarm != null && !Objects.equals(isAlarm, threadPoolNotifyAlarm.getAlarm())) || (activeAlarm != null && !Objects.equals(activeAlarm,
                    threadPoolNotifyAlarm.getActiveAlarm())) || (capacityAlarm != null && !Objects.equals(capacityAlarm, threadPoolNotifyAlarm.getCapacityAlarm()))) {
                checkNotifyAlarm = true;
                threadPoolNotifyAlarm.setAlarm(Optional.ofNullable(isAlarm).orElse(threadPoolNotifyAlarm.getAlarm()));
                threadPoolNotifyAlarm.setActiveAlarm(Optional.ofNullable(activeAlarm).orElse(threadPoolNotifyAlarm.getActiveAlarm()));
                threadPoolNotifyAlarm.setCapacityAlarm(Optional.ofNullable(capacityAlarm).orElse(threadPoolNotifyAlarm.getCapacityAlarm()));
            }
        }
        if (checkNotifyConfig || checkNotifyAlarm) {
            LOG.info("[{}] Dynamic thread pool notification property changes.", executorProperties.getThreadPoolId());
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
                LOG.warn("The queue length cannot be modified. Queue type mismatch. Current queue type: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
    }

    /**
     * Sends a change notification message for a thread pool when its properties are updated.
     * This method logs the changes in thread pool properties and sends a notification to the platform
     * with the updated configuration details.
     *
     * @param executorHolder The holder object for the thread pool executor, containing its current state and properties.
     * @param properties     The new properties for the thread pool that are being applied.
     */
    private void sendChangeNotificationMessage(ThreadPoolExecutorHolder executorHolder, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = executorHolder.getExecutorProperties();
        LOG.info(CHANGE_THREAD_POOL_TEXT,
                executorHolder.getThreadPoolId(),
                String.format(CHANGE_DELIMITER, beforeProperties.getCorePoolSize(), properties.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()),
                String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), properties.getQueueCapacity()),
                String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()),
                String.format(CHANGE_DELIMITER, beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut()),
                String.format(CHANGE_DELIMITER, beforeProperties.getRejectedHandler(), properties.getRejectedHandler()),
                String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut()));

        // Send platform configuration notification
        ChangeParameterNotifyRequest changeRequest = buildChangeRequest(beforeProperties, properties);
        ThreadPoolCheckAlarmSupport.getThreadPoolConfigChangeHandler().sendPoolConfigChange(changeRequest);
    }

    /**
     * Construct change parameter notify request instance.
     *
     * @param beforeProperties old properties
     * @param properties       new properties
     * @return instance
     */
    private ChangeParameterNotifyRequest buildChangeRequest(ExecutorProperties beforeProperties, ExecutorProperties properties) {
        ChangeParameterNotifyRequest changeParameterNotifyRequest = ChangeParameterNotifyRequest.builder()
                .beforeCorePoolSize(beforeProperties.getCorePoolSize())
                .beforeMaximumPoolSize(beforeProperties.getMaximumPoolSize())
                .beforeAllowsCoreThreadTimeOut(beforeProperties.getAllowCoreThreadTimeOut())
                .beforeKeepAliveTime(beforeProperties.getKeepAliveTime())
                .beforeQueueCapacity(beforeProperties.getQueueCapacity())
                .beforeRejectedName(beforeProperties.getRejectedHandler())
                .beforeExecuteTimeOut(beforeProperties.getExecuteTimeOut())
                .blockingQueueName(properties.getBlockingQueue())
                .nowCorePoolSize(Optional.ofNullable(properties.getCorePoolSize()).orElse(beforeProperties.getCorePoolSize()))
                .nowMaximumPoolSize(Optional.ofNullable(properties.getMaximumPoolSize()).orElse(beforeProperties.getMaximumPoolSize()))
                .nowAllowsCoreThreadTimeOut(Optional.ofNullable(properties.getAllowCoreThreadTimeOut()).orElse(beforeProperties.getAllowCoreThreadTimeOut()))
                .nowKeepAliveTime(Optional.ofNullable(properties.getKeepAliveTime()).orElse(beforeProperties.getKeepAliveTime()))
                .nowQueueCapacity(Optional.ofNullable(properties.getQueueCapacity()).orElse(beforeProperties.getQueueCapacity()))
                .nowRejectedName(Optional.ofNullable(properties.getRejectedHandler()).orElse(beforeProperties.getRejectedHandler()))
                .nowExecuteTimeOut(Optional.ofNullable(properties.getExecuteTimeOut()).orElse(beforeProperties.getExecuteTimeOut()))
                .build();
        changeParameterNotifyRequest.setThreadPoolId(beforeProperties.getThreadPoolId());
        return changeParameterNotifyRequest;
    }
}
