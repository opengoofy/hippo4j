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

package cn.hippo4j.core.springboot.starter.support;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.*;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.springboot.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.springboot.starter.config.DynamicThreadPoolNotifyProperties;
import cn.hippo4j.core.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.core.toolkit.inet.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.task.TaskDecorator;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic thread-pool post processor.
 */
@Slf4j
@AllArgsConstructor
public final class DynamicThreadPoolPostProcessor implements BeanPostProcessor {

    private final BootstrapCoreProperties bootstrapCoreProperties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DynamicThreadPoolExecutor || DynamicThreadPoolAdapterChoose.match(bean)) {
            DynamicThreadPool dynamicThreadPool;
            try {
                dynamicThreadPool = ApplicationContextHolder.findAnnotationOnBean(beanName, DynamicThreadPool.class);
                if (Objects.isNull(dynamicThreadPool)) {
                    // Adapt to lower versions of SpringBoot.
                    dynamicThreadPool = DynamicThreadPoolAnnotationUtil.findAnnotationOnBean(beanName, DynamicThreadPool.class);
                    if (Objects.isNull(dynamicThreadPool)) {
                        return bean;
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to create dynamic thread pool in annotation mode.", ex);
                return bean;
            }
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
            if ((dynamicThreadPoolExecutor = DynamicThreadPoolAdapterChoose.unwrap(bean)) == null) {
                dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) bean;
            }
            DynamicThreadPoolWrapper wrap = new DynamicThreadPoolWrapper(dynamicThreadPoolExecutor.getThreadPoolId(), dynamicThreadPoolExecutor);
            ThreadPoolExecutor remoteThreadPoolExecutor = fillPoolAndRegister(wrap);
            DynamicThreadPoolAdapterChoose.replace(bean, remoteThreadPoolExecutor);
            return remoteThreadPoolExecutor;
        }
        if (bean instanceof DynamicThreadPoolWrapper) {
            DynamicThreadPoolWrapper wrap = (DynamicThreadPoolWrapper) bean;
            registerAndSubscribe(wrap);
        }
        return bean;
    }

    /**
     * Register and subscribe.
     *
     * @param dynamicThreadPoolWrap
     */
    protected void registerAndSubscribe(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        fillPoolAndRegister(dynamicThreadPoolWrap);
    }

    /**
     * Fill the thread pool and register.
     *
     * @param dynamicThreadPoolWrapper
     */
    protected ThreadPoolExecutor fillPoolAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        String threadPoolId = dynamicThreadPoolWrapper.getThreadPoolId();
        ThreadPoolExecutor newDynamicPoolExecutor = dynamicThreadPoolWrapper.getExecutor();
        ExecutorProperties executorProperties = null;
        if (null != bootstrapCoreProperties.getExecutors()) {
            executorProperties = bootstrapCoreProperties.getExecutors()
                    .stream()
                    .filter(each -> Objects.equals(threadPoolId, each.getThreadPoolId()))
                    .findFirst()
                    .orElse(null);
            if (executorProperties != null) {
                try {
                    BlockingQueue workQueue = BlockingQueueTypeEnum.createBlockingQueue(executorProperties.getBlockingQueue(), executorProperties.getQueueCapacity());
                    String threadNamePrefix = executorProperties.getThreadNamePrefix();
                    newDynamicPoolExecutor = ThreadPoolBuilder.builder()
                            .dynamicPool()
                            .workQueue(workQueue)
                            .threadFactory(StringUtil.isNotBlank(threadNamePrefix) ? threadNamePrefix : threadPoolId)
                            .executeTimeOut(Optional.ofNullable(executorProperties.getExecuteTimeOut()).orElse(0L))
                            .poolThreadSize(executorProperties.getCorePoolSize(), executorProperties.getMaximumPoolSize())
                            .keepAliveTime(executorProperties.getKeepAliveTime(), TimeUnit.SECONDS)
                            .rejected(RejectedPolicyTypeEnum.createPolicy(executorProperties.getRejectedHandler()))
                            .allowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeOut())
                            .build();
                } catch (Exception ex) {
                    log.error("Failed to initialize thread pool configuration. error: {}", ex);
                } finally {
                    if (Objects.isNull(dynamicThreadPoolWrapper.getExecutor())) {
                        dynamicThreadPoolWrapper.setExecutor(CommonDynamicThreadPool.getInstance(threadPoolId));
                    }
                    dynamicThreadPoolWrapper.setInitFlag(Boolean.TRUE);
                }
            }
            if (dynamicThreadPoolWrapper.getExecutor() instanceof AbstractDynamicExecutorSupport) {
                DynamicThreadPoolNotifyProperties notify = Optional.ofNullable(executorProperties).map(ExecutorProperties::getNotify).orElse(null);
                boolean isAlarm = Optional.ofNullable(executorProperties.getAlarm())
                        .orElseGet(() -> bootstrapCoreProperties.getAlarm() != null ? bootstrapCoreProperties.getAlarm() : true);
                int activeAlarm = Optional.ofNullable(executorProperties.getActiveAlarm())
                        .orElseGet(() -> bootstrapCoreProperties.getActiveAlarm() != null ? bootstrapCoreProperties.getActiveAlarm() : 80);
                int capacityAlarm = Optional.ofNullable(executorProperties.getCapacityAlarm())
                        .orElseGet(() -> bootstrapCoreProperties.getCapacityAlarm() != null ? bootstrapCoreProperties.getCapacityAlarm() : 80);
                int interval = Optional.ofNullable(notify)
                        .map(each -> each.getInterval()).orElseGet(() -> bootstrapCoreProperties.getAlarmInterval() != null ? bootstrapCoreProperties.getAlarmInterval() : 5);
                String receive = Optional.ofNullable(notify)
                        .map(each -> each.getReceives()).orElseGet(() -> StringUtil.isNotEmpty(bootstrapCoreProperties.getReceives()) ? bootstrapCoreProperties.getReceives() : "");
                ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(isAlarm, activeAlarm, capacityAlarm);
                threadPoolNotifyAlarm.setInterval(interval);
                threadPoolNotifyAlarm.setReceives(receive);
                GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);
                TaskDecorator taskDecorator = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrapper.getExecutor()).getTaskDecorator();
                ((DynamicThreadPoolExecutor) newDynamicPoolExecutor).setTaskDecorator(taskDecorator);
                long awaitTerminationMillis = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrapper.getExecutor()).awaitTerminationMillis;
                boolean waitForTasksToCompleteOnShutdown = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrapper.getExecutor()).waitForTasksToCompleteOnShutdown;
                ((DynamicThreadPoolExecutor) newDynamicPoolExecutor).setSupportParam(awaitTerminationMillis, waitForTasksToCompleteOnShutdown);
            }
            dynamicThreadPoolWrapper.setExecutor(newDynamicPoolExecutor);
        }
        GlobalThreadPoolManage.registerPool(dynamicThreadPoolWrapper.getThreadPoolId(), dynamicThreadPoolWrapper);
        GlobalCoreThreadPoolManage.register(
                threadPoolId,
                executorProperties == null
                        ? buildExecutorProperties(threadPoolId, newDynamicPoolExecutor)
                        : executorProperties);
        return newDynamicPoolExecutor;
    }

    /**
     * Build executor properties.
     *
     * @param threadPoolId
     * @param executor
     * @return
     */
    private ExecutorProperties buildExecutorProperties(String threadPoolId, ThreadPoolExecutor executor) {
        ExecutorProperties executorProperties = new ExecutorProperties();
        BlockingQueue<Runnable> blockingQueue = executor.getQueue();
        int queueSize = blockingQueue.size();
        String queueType = blockingQueue.getClass().getSimpleName();
        int remainingCapacity = blockingQueue.remainingCapacity();
        int queueCapacity = queueSize + remainingCapacity;
        executorProperties.setCorePoolSize(executor.getCorePoolSize())
                .setMaximumPoolSize(executor.getMaximumPoolSize())
                .setAllowCoreThreadTimeOut(executor.allowsCoreThreadTimeOut())
                .setKeepAliveTime(executor.getKeepAliveTime(TimeUnit.SECONDS))
                .setBlockingQueue(queueType)
                .setExecuteTimeOut(10000L)
                .setQueueCapacity(queueCapacity)
                .setRejectedHandler(((DynamicThreadPoolExecutor) executor).getRedundancyHandler().getClass().getSimpleName())
                .setThreadPoolId(threadPoolId);
        return executorProperties;
    }
}
