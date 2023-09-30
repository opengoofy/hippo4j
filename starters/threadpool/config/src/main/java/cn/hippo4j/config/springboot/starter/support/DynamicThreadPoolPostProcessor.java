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

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.executor.ExecutorNotifyProperties;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.toolkit.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.message.core.service.GlobalNotifyAlarmManage;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolNotifyAlarm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

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

    private static final int DEFAULT_ACTIVE_ALARM = 80;

    private static final int DEFAULT_CAPACITY_ALARM = 80;

    private static final int DEFAULT_INTERVAL = 5;

    private static final String DEFAULT_RECEIVES = "";

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
            ThreadPoolExecutor dynamicThreadPoolExecutor = DynamicThreadPoolAdapterChoose.unwrap(bean);
            if (dynamicThreadPoolExecutor == null) {
                dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) bean;
            }
            ThreadPoolExecutor remoteThreadPoolExecutor = fillPoolAndRegister(((DynamicThreadPoolExecutor) dynamicThreadPoolExecutor).getThreadPoolId(), dynamicThreadPoolExecutor);
            DynamicThreadPoolAdapterChoose.replace(bean, remoteThreadPoolExecutor);
            return DynamicThreadPoolAdapterChoose.match(bean) ? bean : remoteThreadPoolExecutor;
        }
        return bean;
    }

    /**
     * Fill the thread pool and register.
     *
     * @param threadPoolId dynamic thread-pool id
     * @param executor     dynamic thread-pool executor
     */
    protected ThreadPoolExecutor fillPoolAndRegister(String threadPoolId, ThreadPoolExecutor executor) {
        ExecutorProperties executorProperties = null;
        if (configProperties.getExecutors() != null) {
            executorProperties = configProperties.getExecutors()
                    .stream()
                    .filter(each -> Objects.equals(threadPoolId, each.getThreadPoolId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("The thread pool id does not exist in the configuration."));
            try {
                executorProperties = buildActualExecutorProperties(executorProperties);
                threadPoolParamReplace(executor, executorProperties);
            } catch (Exception ex) {
                log.error("Failed to initialize thread pool configuration.", ex);
            }
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = buildThreadPoolNotifyAlarm(executorProperties);
            GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);
        }
        ThreadPoolExecutorRegistry.putHolder(threadPoolId, executor,
                executorProperties == null
                        ? buildDefaultExecutorProperties(threadPoolId, executor)
                        : executorProperties);
        // GlobalThreadPoolManage.registerPool(dynamicThreadPoolWrapper.getThreadPoolId(), dynamicThreadPoolWrapper);
        // GlobalConfigThreadPoolManage.register(
        // threadPoolId,
        // executorProperties == null
        // ? buildDefaultExecutorProperties(threadPoolId, executor)
        // : executorProperties);
        return executor;
    }

    /**
     * Build actual executor properties.
     *
     * @param executorProperties executor properties
     * @return executor properties
     */
    private ExecutorProperties buildActualExecutorProperties(ExecutorProperties executorProperties) {
        return Optional.ofNullable(configProperties.getDefaultExecutor()).map(each -> buildExecutorProperties(executorProperties)).orElse(executorProperties);
    }

    /**
     * Build default executor properties.
     *
     * @param threadPoolId thread-pool id
     * @param executor     dynamic thread-pool executor
     * @return executor properties
     */
    private ExecutorProperties buildDefaultExecutorProperties(String threadPoolId, ThreadPoolExecutor executor) {
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
                .setExecuteTimeOut(Constants.EXECUTE_TIME_OUT)
                .setQueueCapacity(queueCapacity)
                .setRejectedHandler(executor.getRejectedExecutionHandler().getClass().getSimpleName())
                .setThreadPoolId(threadPoolId);
        return executorProperties;
    }

    /**
     * Thread-pool param replace.
     *
     * @param executor           dynamic thread-pool executor
     * @param executorProperties executor properties
     */
    private void threadPoolParamReplace(ThreadPoolExecutor executor, ExecutorProperties executorProperties) {
        BlockingQueue workQueue = BlockingQueueTypeEnum.createBlockingQueue(executorProperties.getBlockingQueue(), executorProperties.getQueueCapacity());
        ReflectUtil.setFieldValue(executor, "workQueue", workQueue);
        // fix https://github.com/opengoofy/hippo4j/issues/1063
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, executorProperties.getCorePoolSize(), executorProperties.getMaximumPoolSize());
        executor.setKeepAliveTime(executorProperties.getKeepAliveTime(), TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeOut());
        executor.setRejectedExecutionHandler(RejectedPolicyTypeEnum.createPolicy(executorProperties.getRejectedHandler()));
        if (executor instanceof DynamicThreadPoolExecutor) {
            Optional.ofNullable(executorProperties.getExecuteTimeOut())
                    .ifPresent(executeTimeOut -> ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(executeTimeOut));
        }
    }

    /**
     * Build executor properties.
     *
     * @param executorProperties executor properties
     * @return executor properties
     */
    private ExecutorProperties buildExecutorProperties(ExecutorProperties executorProperties) {
        return ExecutorProperties.builder()
                .corePoolSize(Optional.ofNullable(executorProperties.getCorePoolSize())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCorePoolSize).get()))
                .maximumPoolSize(Optional.ofNullable(executorProperties.getMaximumPoolSize())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getMaximumPoolSize).get()))
                .allowCoreThreadTimeOut(Optional.ofNullable(executorProperties.getAllowCoreThreadTimeOut())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getAllowCoreThreadTimeOut).get()))
                .keepAliveTime(Optional.ofNullable(executorProperties.getKeepAliveTime())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getKeepAliveTime).get()))
                .blockingQueue(Optional.ofNullable(executorProperties.getBlockingQueue())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getBlockingQueue).get()))
                .executeTimeOut(Optional.ofNullable(executorProperties.getExecuteTimeOut())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getExecuteTimeOut).orElse(0L)))
                .queueCapacity(Optional.ofNullable(executorProperties.getQueueCapacity())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getQueueCapacity).get()))
                .rejectedHandler(Optional.ofNullable(executorProperties.getRejectedHandler())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getRejectedHandler).get()))
                .threadNamePrefix(StringUtil.isBlank(executorProperties.getThreadNamePrefix()) ? executorProperties.getThreadPoolId() : executorProperties.getThreadNamePrefix())
                .threadPoolId(executorProperties.getThreadPoolId())
                .alarm(Optional.ofNullable(executorProperties.getAlarm())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getAlarm).orElse(null)))
                .activeAlarm(Optional.ofNullable(executorProperties.getActiveAlarm())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getActiveAlarm).orElse(null)))
                .capacityAlarm(Optional.ofNullable(executorProperties.getCapacityAlarm())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCapacityAlarm).orElse(null)))
                .notify(Optional.ofNullable(executorProperties.getNotify())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).orElse(null)))
                .nodes(Optional.ofNullable(executorProperties.getNodes())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNodes).orElse(null)))
                .build();
    }

    /**
     * Build thread-pool notify alarm
     *
     * @param executorProperties executor properties
     * @return thread-pool notify alarm
     */
    private ThreadPoolNotifyAlarm buildThreadPoolNotifyAlarm(ExecutorProperties executorProperties) {
        ExecutorNotifyProperties notify = Optional.ofNullable(executorProperties).map(ExecutorProperties::getNotify).orElse(null);
        boolean isAlarm = Optional.ofNullable(executorProperties.getAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getAlarm).orElse(true));
        int activeAlarm = Optional.ofNullable(executorProperties.getActiveAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getActiveAlarm).orElse(DEFAULT_ACTIVE_ALARM));
        int capacityAlarm = Optional.ofNullable(executorProperties.getCapacityAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCapacityAlarm).orElse(DEFAULT_CAPACITY_ALARM));
        int interval = Optional.ofNullable(notify)
                .map(ExecutorNotifyProperties::getInterval)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(ExecutorNotifyProperties::getInterval).orElse(DEFAULT_INTERVAL));
        String receive = Optional.ofNullable(notify)
                .map(ExecutorNotifyProperties::getReceives)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(ExecutorNotifyProperties::getReceives).orElse(DEFAULT_RECEIVES));
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(isAlarm, activeAlarm, capacityAlarm);
        threadPoolNotifyAlarm.setInterval(interval);
        threadPoolNotifyAlarm.setReceives(receive);
        return threadPoolNotifyAlarm;
    }
}
