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

package cn.hippo4j.config.springboot.starter.refresher.event;

import cn.hippo4j.common.api.ThreadPoolConfigChange;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.config.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.config.springboot.starter.notify.ConfigModeNotifyConfigBuilder;
import cn.hippo4j.config.springboot.starter.support.GlobalCoreThreadPoolManage;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.message.service.GlobalNotifyAlarmManage;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_THREAD_POOL_TEXT;
import static cn.hippo4j.config.springboot.starter.refresher.event.Hippo4jConfigDynamicRefreshEventOrder.EXECUTORS_LISTENER;

/**
 * Dynamic thread-pool refresh listener.
 */
@Slf4j
@RequiredArgsConstructor
@Order(EXECUTORS_LISTENER)
public class DynamicThreadPoolRefreshListener extends AbstractRefreshListener<ExecutorProperties> {

    private final ThreadPoolConfigChange threadPoolConfigChange;

    private final ConfigModeNotifyConfigBuilder configModeNotifyConfigBuilder;

    private final Hippo4jBaseSendMessageService hippo4jBaseSendMessageService;

    @Override
    public String getNodes(ExecutorProperties properties) {
        return properties.getNodes();
    }

    @Override
    public void onApplicationEvent(Hippo4jConfigDynamicRefreshEvent event) {
        BootstrapConfigProperties bindableConfigProperties = event.getBootstrapConfigProperties();
        List<ExecutorProperties> executors = bindableConfigProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            if (!match(properties)) {
                continue;
            }
            /*
             * Check whether the notification configuration is consistent, this operation will not trigger the notification.
             */
            checkNotifyConsistencyAndReplace(properties);
            if (!checkConsistency(threadPoolId, properties)) {
                continue;
            }
            dynamicRefreshPool(threadPoolId, properties);
            ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
            GlobalCoreThreadPoolManage.refresh(threadPoolId, failDefaultExecutorProperties(beforeProperties, properties));
            ChangeParameterNotifyRequest changeRequest = buildChangeRequest(beforeProperties, properties);
            log.info(CHANGE_THREAD_POOL_TEXT,
                    threadPoolId,
                    String.format(CHANGE_DELIMITER, beforeProperties.getCorePoolSize(), changeRequest.getNowCorePoolSize()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), changeRequest.getNowMaximumPoolSize()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), changeRequest.getNowQueueCapacity()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(), changeRequest.getNowKeepAliveTime()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getExecuteTimeOut(), changeRequest.getNowExecuteTimeOut()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getRejectedHandler(), changeRequest.getNowRejectedName()),
                    String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(), changeRequest.getNowAllowsCoreThreadTimeOut()));
            try {
                threadPoolConfigChange.sendPoolConfigChange(changeRequest);
            } catch (Throwable ex) {
                log.error("Failed to send Chang smart application listener notice. Message: {}", ex.getMessage());
            }
        }
    }

    /**
     * Fail default executor properties.
     *
     * @param beforeProperties old properties
     * @param properties       new properties
     * @return executor properties
     */
    private ExecutorProperties failDefaultExecutorProperties(ExecutorProperties beforeProperties, ExecutorProperties properties) {
        return ExecutorProperties.builder()
                .corePoolSize(Optional.ofNullable(properties.getCorePoolSize()).orElse(beforeProperties.getCorePoolSize()))
                .maximumPoolSize(Optional.ofNullable(properties.getMaximumPoolSize()).orElse(beforeProperties.getMaximumPoolSize()))
                .blockingQueue(properties.getBlockingQueue())
                .queueCapacity(Optional.ofNullable(properties.getQueueCapacity()).orElse(beforeProperties.getQueueCapacity()))
                .keepAliveTime(Optional.ofNullable(properties.getKeepAliveTime()).orElse(beforeProperties.getKeepAliveTime()))
                .executeTimeOut(Optional.ofNullable(properties.getExecuteTimeOut()).orElse(beforeProperties.getExecuteTimeOut()))
                .rejectedHandler(Optional.ofNullable(properties.getRejectedHandler()).orElse(beforeProperties.getRejectedHandler()))
                .allowCoreThreadTimeOut(Optional.ofNullable(properties.getAllowCoreThreadTimeOut()).orElse(beforeProperties.getAllowCoreThreadTimeOut()))
                .threadPoolId(beforeProperties.getThreadPoolId())
                .build();
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

    /**
     * Check notify consistency and replace.
     *
     * @param executorProperties
     */
    private void checkNotifyConsistencyAndReplace(ExecutorProperties executorProperties) {
        boolean checkNotifyConfig = false;
        boolean checkNotifyAlarm = false;
        List<String> changeKeys = new ArrayList<>();
        Map<String, List<NotifyConfigDTO>> newDynamicThreadPoolNotifyMap =
                configModeNotifyConfigBuilder.buildSingleNotifyConfig(executorProperties);
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
            configModeNotifyConfigBuilder.initCacheAndLock(newDynamicThreadPoolNotifyMap);
            hippo4jBaseSendMessageService.putPlatform(newDynamicThreadPoolNotifyMap);
        }
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(executorProperties.getThreadPoolId());
        if (threadPoolNotifyAlarm != null) {
            Boolean isAlarm = executorProperties.getAlarm();
            Integer activeAlarm = executorProperties.getActiveAlarm();
            Integer capacityAlarm = executorProperties.getCapacityAlarm();
            // FIXME Compare using Objects.equals
            if ((isAlarm != null && isAlarm != threadPoolNotifyAlarm.getAlarm())
                    || (activeAlarm != null && activeAlarm != threadPoolNotifyAlarm.getActiveAlarm())
                    || (capacityAlarm != null && capacityAlarm != threadPoolNotifyAlarm.getCapacityAlarm())) {
                checkNotifyAlarm = true;
                threadPoolNotifyAlarm.setAlarm(Optional.ofNullable(isAlarm).orElse(threadPoolNotifyAlarm.getAlarm()));
                threadPoolNotifyAlarm.setActiveAlarm(Optional.ofNullable(activeAlarm).orElse(threadPoolNotifyAlarm.getActiveAlarm()));
                threadPoolNotifyAlarm.setCapacityAlarm(Optional.ofNullable(capacityAlarm).orElse(threadPoolNotifyAlarm.getCapacityAlarm()));
            }
        }
        if (checkNotifyConfig || checkNotifyAlarm) {
            log.info("[{}] Dynamic thread pool notification property changes.", executorProperties.getThreadPoolId());
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
     * Dynamic refresh pool.
     *
     * @param threadPoolId
     * @param properties
     */
    private void dynamicRefreshPool(String threadPoolId, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();
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
        if (properties.getExecuteTimeOut() != null && !Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut())) {
            if (executor instanceof DynamicThreadPoolExecutor) {
                ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(properties.getExecuteTimeOut());
            }
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
