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
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.*;
import cn.hippo4j.core.springboot.starter.config.BootstrapCoreProperties;
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
 *
 * @author chen.ma
 * @date 2021/8/2 20:40
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
        if (bean instanceof DynamicThreadPoolExecutor) {
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
            DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) bean;
            DynamicThreadPoolWrapper wrap = new DynamicThreadPoolWrapper(dynamicExecutor.getThreadPoolId(), dynamicExecutor);
            ThreadPoolExecutor remoteExecutor = fillPoolAndRegister(wrap);
            return remoteExecutor;
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
     * @param dynamicThreadPoolWrap
     */
    protected ThreadPoolExecutor fillPoolAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        String threadPoolId = dynamicThreadPoolWrap.getThreadPoolId();
        ThreadPoolExecutor newDynamicPoolExecutor = dynamicThreadPoolWrap.getExecutor();
        ExecutorProperties executorProperties = null;
        if (null != bootstrapCoreProperties.getExecutors()) {
            executorProperties = bootstrapCoreProperties.getExecutors()
                    .stream()
                    .filter(each -> Objects.equals(threadPoolId, each.getThreadPoolId()))
                    .findFirst()
                    .orElse(null);
            if (executorProperties != null) {
                try {
                    BlockingQueue workQueue = QueueTypeEnum.createBlockingQueue(executorProperties.getBlockingQueue(), executorProperties.getQueueCapacity());
                    String threadNamePrefix = executorProperties.getThreadNamePrefix();
                    newDynamicPoolExecutor = ThreadPoolBuilder.builder()
                            .dynamicPool()
                            .workQueue(workQueue)
                            .threadFactory(StringUtil.isNotBlank(threadNamePrefix) ? threadNamePrefix : threadPoolId)
                            .executeTimeOut(Optional.ofNullable(executorProperties.getExecuteTimeOut()).orElse(0L))
                            .poolThreadSize(executorProperties.getCorePoolSize(), executorProperties.getMaximumPoolSize())
                            .keepAliveTime(executorProperties.getKeepAliveTime(), TimeUnit.SECONDS)
                            .rejected(RejectedTypeEnum.createPolicy(executorProperties.getRejectedHandler()))
                            .allowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeOut())
                            .build();
                } catch (Exception ex) {
                    log.error("Failed to initialize thread pool configuration. error :: {}", ex);
                } finally {
                    if (Objects.isNull(dynamicThreadPoolWrap.getExecutor())) {
                        dynamicThreadPoolWrap.setExecutor(CommonDynamicThreadPool.getInstance(threadPoolId));
                    }
                    dynamicThreadPoolWrap.setInitFlag(Boolean.TRUE);
                }
            }

            if (dynamicThreadPoolWrap.getExecutor() instanceof AbstractDynamicExecutorSupport) {
                ThreadPoolNotifyAlarm notify = Optional.ofNullable(executorProperties).map(ExecutorProperties::getNotify).orElse(null);
                boolean isAlarm = Optional.ofNullable(notify)
                        .map(each -> each.getIsAlarm()).orElseGet(() -> bootstrapCoreProperties.getAlarm() != null ? bootstrapCoreProperties.getAlarm() : true);
                int activeAlarm = Optional.ofNullable(notify)
                        .map(each -> each.getActiveAlarm()).orElseGet(() -> bootstrapCoreProperties.getActiveAlarm() != null ? bootstrapCoreProperties.getActiveAlarm() : 80);
                int capacityAlarm = Optional.ofNullable(notify)
                        .map(each -> each.getActiveAlarm()).orElseGet(() -> bootstrapCoreProperties.getCapacityAlarm() != null ? bootstrapCoreProperties.getCapacityAlarm() : 80);
                int interval = Optional.ofNullable(notify)
                        .map(each -> each.getInterval()).orElseGet(() -> bootstrapCoreProperties.getAlarmInterval() != null ? bootstrapCoreProperties.getAlarmInterval() : 5);
                String receive = Optional.ofNullable(notify)
                        .map(each -> each.getReceive()).orElseGet(() -> bootstrapCoreProperties.getReceive() != null ? bootstrapCoreProperties.getReceive() : null);
                ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(isAlarm, activeAlarm, capacityAlarm);
                threadPoolNotifyAlarm.setInterval(interval);
                threadPoolNotifyAlarm.setReceive(receive);
                GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);

                TaskDecorator taskDecorator = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).getTaskDecorator();
                ((DynamicThreadPoolExecutor) newDynamicPoolExecutor).setTaskDecorator(taskDecorator);

                long awaitTerminationMillis = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).awaitTerminationMillis;
                boolean waitForTasksToCompleteOnShutdown = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).waitForTasksToCompleteOnShutdown;
                ((DynamicThreadPoolExecutor) newDynamicPoolExecutor).setSupportParam(awaitTerminationMillis, waitForTasksToCompleteOnShutdown);
            }
            dynamicThreadPoolWrap.setExecutor(newDynamicPoolExecutor);
        }

        GlobalThreadPoolManage.registerPool(dynamicThreadPoolWrap.getThreadPoolId(), dynamicThreadPoolWrap);
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
        BlockingQueue<Runnable> queue = executor.getQueue();
        int queueSize = queue.size();
        String queueType = queue.getClass().getSimpleName();
        int remainingCapacity = queue.remainingCapacity();
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
