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

package cn.hippo4j.agent.plugin.spring.common.support;

import cn.hippo4j.agent.core.util.ReflectUtil;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.handler.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.common.model.executor.ExecutorNotifyProperties;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.message.core.service.GlobalNotifyAlarmManage;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolNotifyAlarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.DYNAMIC_THREAD_POOL_EXECUTOR;

/**
 * Spring thread pool register support
 */
public class SpringThreadPoolRegisterSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringThreadPoolRegisterSupport.class);

    private static final int DEFAULT_ACTIVE_ALARM = 80;

    private static final int DEFAULT_CAPACITY_ALARM = 80;

    private static final int DEFAULT_INTERVAL = 5;

    private static final String DEFAULT_RECEIVES = "";

    public static void registerThreadPoolInstances(ApplicationContext context) {
        Map<ThreadPoolExecutor, Class<?>> referencedClassMap = ThreadPoolExecutorRegistry.REFERENCED_CLASS_MAP;
        for (Map.Entry<ThreadPoolExecutor, Class<?>> entry : referencedClassMap.entrySet()) {
            ThreadPoolExecutor enhancedInstance = entry.getKey();
            Class<?> declaredClass = entry.getValue();
            List<Field> declaredFields = ReflectUtil.getStaticFieldsFromType(declaredClass, ThreadPoolExecutor.class);
            for (Field field : declaredFields) {
                try {
                    Object value = field.get(null);
                    if (value == enhancedInstance) {
                        String threadPoolId = declaredClass.getName() + "#" + field.getName();
                        register(threadPoolId, enhancedInstance, Boolean.TRUE);
                        break;
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Get static field error.", e);
                }
            }
        }

        Map<String, Executor> beansWithAnnotation = context.getBeansOfType(Executor.class);
        beansWithAnnotation.forEach((beanName, bean) -> {
            ThreadPoolExecutor executor = null;
            // Get ThreadPoolExecutor Instance
            if (DynamicThreadPoolAdapterChoose.match(bean)) {
                executor = DynamicThreadPoolAdapterChoose.unwrap(bean);
            } else if (bean instanceof ThreadPoolTaskExecutor) {
                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) bean;
                // Get a real instance of ThreadPoolExecutor
                executor = taskExecutor.getThreadPoolExecutor();
            } else if (bean instanceof ThreadPoolExecutor) {
                executor = (ThreadPoolExecutor) bean;
            } else {
                LOGGER.warn("[Hippo4j-Agent] Thread pool ignore registered beanName={}, Now Unsupported thread pool executor type:{} ", beanName, bean.getClass().getName());
            }
            if (executor == null) {
                LOGGER.warn("[Hippo4j-Agent] Thread pool is null, ignore bean registration. beanName={}, beanClass={}", beanName, bean.getClass().getName());
            } else {
                register(beanName, executor, Boolean.FALSE);
            }
        });

        LOGGER.info("[Hippo4j-Agent] Registered thread pool instances successfully.");
    }

    public static void register(String threadPoolId, ThreadPoolExecutor executor, Boolean isAgentScanEnhancePool) {
        if (executor == null) {
            return;
        }
        ExecutorProperties executorProperties =
                SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES.getExecutors().stream().filter(each -> Objects.equals(threadPoolId, each.getThreadPoolId())).findFirst().orElse(null);

        // Determines the thread pool that is currently obtained by bean scanning
        if (Objects.isNull(executorProperties)) {
            if (isAgentScanEnhancePool) {
                throw new RuntimeException(String.format("The thread pool id [%s] does not exist in the configuration.", threadPoolId));
            } else {
                // Thread pool that do not require enhancement are skipped by the agent
                return;
            }
        }

        try {
            executorProperties = buildActualExecutorProperties(executorProperties);
            // Replace the original configuration and refresh the thread pool
            threadPoolParamReplace(executor, executorProperties);
        } catch (Exception ex) {
            LOGGER.error("[Hippo4j-Agent] Failed to initialize thread pool configuration.", ex);
        }
        // Build notification information entity
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = buildThreadPoolNotifyAlarm(executorProperties);
        GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);

        ThreadPoolExecutorRegistry.putHolder(threadPoolId, executor, executorProperties);
    }

    /**
     * Thread-pool param replace.
     *
     * @param executor           dynamic thread-pool executor
     * @param executorProperties executor properties
     */
    private static void threadPoolParamReplace(ThreadPoolExecutor executor, ExecutorProperties executorProperties) {
        BlockingQueue workQueue = BlockingQueueTypeEnum.createBlockingQueue(executorProperties.getBlockingQueue(), executorProperties.getQueueCapacity());
        cn.hippo4j.common.toolkit.ReflectUtil.setFieldValue(executor, "workQueue", workQueue);
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, executorProperties.getCorePoolSize(), executorProperties.getMaximumPoolSize());
        executor.setKeepAliveTime(executorProperties.getKeepAliveTime(), TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeOut());
        executor.setRejectedExecutionHandler(RejectedPolicyTypeEnum.createPolicy(executorProperties.getRejectedHandler()));
        // Reflection sets the thread pool setExecuteTimeOut
        if (DYNAMIC_THREAD_POOL_EXECUTOR.equals(executor.getClass().getName())) {
            try {
                Method setExecuteTimeOutMethod = executor.getClass().getMethod("setExecuteTimeOut", Long.class);
                Long executeTimeOut = executorProperties.getExecuteTimeOut();
                if (executeTimeOut != null) {
                    setExecuteTimeOutMethod.invoke(executor, executeTimeOut);
                }
            } catch (Exception e) {
                LOGGER.error("[Hippo4j-Agent] Failed to set executeTimeOut.", e);
            }
        }
    }

    /**
     * Build actual executor properties.
     *
     * @param executorProperties executor properties
     * @return executor properties
     */
    private static ExecutorProperties buildActualExecutorProperties(ExecutorProperties executorProperties) {
        return Optional.ofNullable(SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES.getDefaultExecutor()).map(each -> buildExecutorProperties(executorProperties)).orElse(executorProperties);
    }

    /**
     * Build executor properties.
     *
     * @param executorProperties executor properties
     * @return executor properties
     */
    private static ExecutorProperties buildExecutorProperties(ExecutorProperties executorProperties) {
        BootstrapConfigProperties configProperties = SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES;
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
                .alarm(Optional.ofNullable(executorProperties.getAlarm()).orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getAlarm).orElse(null)))
                .activeAlarm(Optional.ofNullable(executorProperties.getActiveAlarm())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getActiveAlarm).orElse(null)))
                .capacityAlarm(Optional.ofNullable(executorProperties.getCapacityAlarm())
                        .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCapacityAlarm).orElse(null)))
                .notify(Optional.ofNullable(executorProperties.getNotify()).orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).orElse(null)))
                .nodes(Optional.ofNullable(executorProperties.getNodes()).orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNodes).orElse(null)))
                .build();
    }

    /**
     * Build thread-pool notify alarm
     *
     * @param executorProperties executor properties
     * @return thread-pool notify alarm
     */
    private static ThreadPoolNotifyAlarm buildThreadPoolNotifyAlarm(ExecutorProperties executorProperties) {
        BootstrapConfigProperties configProperties = SpringPropertiesLoader.BOOTSTRAP_CONFIG_PROPERTIES;
        ExecutorNotifyProperties notify = Optional.ofNullable(executorProperties).map(ExecutorProperties::getNotify).orElse(null);
        boolean isAlarm = Optional.ofNullable(executorProperties.getAlarm()).orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getAlarm).orElse(true));
        int activeAlarm = Optional.ofNullable(executorProperties.getActiveAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getActiveAlarm).orElse(DEFAULT_ACTIVE_ALARM));
        int capacityAlarm = Optional.ofNullable(executorProperties.getCapacityAlarm())
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getCapacityAlarm).orElse(DEFAULT_CAPACITY_ALARM));
        int interval = Optional.ofNullable(notify).map(ExecutorNotifyProperties::getInterval)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(ExecutorNotifyProperties::getInterval).orElse(DEFAULT_INTERVAL));
        String receive = Optional.ofNullable(notify).map(ExecutorNotifyProperties::getReceives)
                .orElseGet(() -> Optional.ofNullable(configProperties.getDefaultExecutor()).map(ExecutorProperties::getNotify).map(ExecutorNotifyProperties::getReceives).orElse(DEFAULT_RECEIVES));
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(isAlarm, activeAlarm, capacityAlarm);
        threadPoolNotifyAlarm.setInterval(interval);
        threadPoolNotifyAlarm.setReceives(receive);
        return threadPoolNotifyAlarm;
    }

}
