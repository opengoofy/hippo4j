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

package cn.hippo4j.config.springboot.starter.support;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.config.springboot.starter.config.DynamicThreadPoolNotifyProperties;
import cn.hippo4j.config.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.CommonDynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.toolkit.DynamicThreadPoolAnnotationUtil;
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

    private final BootstrapConfigProperties configProperties;

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
            return DynamicThreadPoolAdapterChoose.match(bean) ? bean : remoteThreadPoolExecutor;
        }
        if (bean instanceof DynamicThreadPoolWrapper) {
            DynamicThreadPoolWrapper wrap = (DynamicThreadPoolWrapper) bean;
            fillPoolAndRegister(wrap);
        }
        return bean;
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
        if (configProperties.getExecutors() != null) {
            executorProperties = configProperties.getExecutors()
                    .stream()
                    .filter(each -> Objects.equals(threadPoolId, each.getThreadPoolId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("The thread pool id does not exist in the configuration."));
            try {
                newDynamicPoolExecutor = buildNewDynamicThreadPool(executorProperties);
            } catch (Exception ex) {
                log.error("Failed to initialize thread pool configuration. error: {}", ex);
            } finally {
                if (Objects.isNull(dynamicThreadPoolWrapper.getExecutor())) {
                    dynamicThreadPoolWrapper.setExecutor(CommonDynamicThreadPool.getInstance(threadPoolId));
                }
                dynamicThreadPoolWrapper.setInitFlag(Boolean.TRUE);
            }
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = buildThreadPoolNotifyAlarm(executorProperties);
            GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);
            DynamicThreadPoolExecutor actualDynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) dynamicThreadPoolWrapper.getExecutor();
            TaskDecorator taskDecorator = actualDynamicThreadPoolExecutor.getTaskDecorator();
            ((DynamicThreadPoolExecutor) newDynamicPoolExecutor).setTaskDecorator(taskDecorator);
            long awaitTerminationMillis = actualDynamicThreadPoolExecutor.awaitTerminationMillis;
            boolean waitForTasksToCompleteOnShutdown = actualDynamicThreadPoolExecutor.waitForTasksToCompleteOnShutdown;
            ((DynamicThreadPoolExecutor) newDynamicPoolExecutor).setSupportParam(awaitTerminationMillis, waitForTasksToCompleteOnShutdown);
            dynamicThreadPoolWrapper.setExecutor(newDynamicPoolExecutor);
        }
        GlobalThreadPoolManage.registerPool(dynamicThreadPoolWrapper.getThreadPoolId(), dynamicThreadPoolWrapper);
        GlobalCoreThreadPoolManage.register(
                threadPoolId,
                executorProperties == null
                        ? buildExecutorProperties(threadPoolId, newDynamicPoolExecutor)
                        : buildActualExecutorProperties(executorProperties));
        return newDynamicPoolExecutor;
    }

    private ExecutorProperties buildActualExecutorProperties(ExecutorProperties executorProperties) {
        return Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> buildExecutorProperties(executorProperties)).orElse(executorProperties);
    }

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

    private ThreadPoolExecutor buildNewDynamicThreadPool(ExecutorProperties executorProperties) {
        String threadNamePrefix = executorProperties.getThreadNamePrefix();
        ExecutorProperties newExecutorProperties = buildExecutorProperties(executorProperties);
        ThreadPoolExecutor newDynamicPoolExecutor = ThreadPoolBuilder.builder()
                .threadPoolId(executorProperties.getThreadPoolId())
                .threadFactory(StringUtil.isNotBlank(threadNamePrefix) ? threadNamePrefix : executorProperties.getThreadPoolId())
                .poolThreadSize(newExecutorProperties.getCorePoolSize(), newExecutorProperties.getMaximumPoolSize())
                .workQueue(BlockingQueueTypeEnum.createBlockingQueue(newExecutorProperties.getBlockingQueue(), newExecutorProperties.getQueueCapacity()))
                .executeTimeOut(newExecutorProperties.getExecuteTimeOut())
                .keepAliveTime(newExecutorProperties.getKeepAliveTime(), TimeUnit.SECONDS)
                .rejected(RejectedPolicyTypeEnum.createPolicy(newExecutorProperties.getRejectedHandler()))
                .allowCoreThreadTimeOut(newExecutorProperties.getAllowCoreThreadTimeOut())
                .dynamicPool()
                .build();
        return newDynamicPoolExecutor;
    }

    private ExecutorProperties buildExecutorProperties(ExecutorProperties executorProperties) {
        ExecutorProperties newExecutorProperties = ExecutorProperties.builder()
                .corePoolSize(Optional.ofNullable(executorProperties.getCorePoolSize())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getCorePoolSize()).get()))
                .maximumPoolSize(Optional.ofNullable(executorProperties.getMaximumPoolSize())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getMaximumPoolSize()).get()))
                .allowCoreThreadTimeOut(Optional.ofNullable(executorProperties.getAllowCoreThreadTimeOut())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getAllowCoreThreadTimeOut()).get()))
                .keepAliveTime(Optional.ofNullable(executorProperties.getKeepAliveTime())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getKeepAliveTime()).get()))
                .blockingQueue(Optional.ofNullable(executorProperties.getBlockingQueue())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getBlockingQueue()).get()))
                .executeTimeOut(Optional.ofNullable(executorProperties.getExecuteTimeOut())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getExecuteTimeOut()).orElse(0L)))
                .queueCapacity(Optional.ofNullable(executorProperties.getQueueCapacity())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getQueueCapacity()).get()))
                .rejectedHandler(Optional.ofNullable(executorProperties.getRejectedHandler())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> each.getRejectedHandler()).get()))
                .threadNamePrefix(StringUtil.isBlank(executorProperties.getThreadNamePrefix()) ? executorProperties.getThreadPoolId() : executorProperties.getThreadNamePrefix())
                .threadPoolId(executorProperties.getThreadPoolId())
                .build();
        return newExecutorProperties;
    }

    private ThreadPoolNotifyAlarm buildThreadPoolNotifyAlarm(ExecutorProperties executorProperties) {
        DynamicThreadPoolNotifyProperties notify = Optional.ofNullable(executorProperties).map(ExecutorProperties::getNotify).orElse(null);
        boolean isAlarm = Optional.ofNullable(executorProperties.getAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getAlarm).orElse(true));
        int activeAlarm = Optional.ofNullable(executorProperties.getActiveAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getActiveAlarm).orElse(80));
        int capacityAlarm = Optional.ofNullable(executorProperties.getCapacityAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCapacityAlarm).orElse(80));
        int interval = Optional.ofNullable(notify)
                .map(DynamicThreadPoolNotifyProperties::getInterval)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(DynamicThreadPoolNotifyProperties::getInterval).orElse(5));
        String receive = Optional.ofNullable(notify)
                .map(DynamicThreadPoolNotifyProperties::getReceives)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(DynamicThreadPoolNotifyProperties::getReceives).orElse(""));
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(isAlarm, activeAlarm, capacityAlarm);
        threadPoolNotifyAlarm.setInterval(interval);
        threadPoolNotifyAlarm.setReceives(receive);
        return threadPoolNotifyAlarm;
    }
}
