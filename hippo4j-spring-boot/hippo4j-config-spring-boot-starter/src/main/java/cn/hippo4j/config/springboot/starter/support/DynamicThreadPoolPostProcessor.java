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
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.common.api.ExecutorNotifyProperties;
import cn.hippo4j.config.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.toolkit.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.message.service.GlobalNotifyAlarmManage;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
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
     * @param dynamicThreadPoolWrapper dynamic thread-pool wrapper
     */
    protected ThreadPoolExecutor fillPoolAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        String threadPoolId = dynamicThreadPoolWrapper.getThreadPoolId();
        ThreadPoolExecutor executor = dynamicThreadPoolWrapper.getExecutor();
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
            } finally {
                dynamicThreadPoolWrapper.setInitFlag(Boolean.TRUE);
            }
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = buildThreadPoolNotifyAlarm(executorProperties);
            GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);
        }
        GlobalThreadPoolManage.registerPool(dynamicThreadPoolWrapper.getThreadPoolId(), dynamicThreadPoolWrapper);
        GlobalCoreThreadPoolManage.register(
                threadPoolId,
                executorProperties == null
                        ? buildDefaultExecutorProperties(threadPoolId, executor)
                        : executorProperties);
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
                .setExecuteTimeOut(10000L)
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
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getActiveAlarm).orElse(80));
        int capacityAlarm = Optional.ofNullable(executorProperties.getCapacityAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCapacityAlarm).orElse(80));
        int interval = Optional.ofNullable(notify)
                .map(ExecutorNotifyProperties::getInterval)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(ExecutorNotifyProperties::getInterval).orElse(5));
        String receive = Optional.ofNullable(notify)
                .map(ExecutorNotifyProperties::getReceives)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(ExecutorNotifyProperties::getReceives).orElse(""));
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(isAlarm, activeAlarm, capacityAlarm);
        threadPoolNotifyAlarm.setInterval(interval);
        threadPoolNotifyAlarm.setReceives(receive);
        return threadPoolNotifyAlarm;
    }
}
