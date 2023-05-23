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

package cn.hippo4j.springboot.starter.support;

import cn.hippo4j.common.model.Result;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.extension.enums.EnableEnum;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.toolkit.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.message.service.GlobalNotifyAlarmManage;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.core.DynamicThreadPoolSubscribeConfig;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.INITIAL_CAPACITY;
import static cn.hippo4j.common.constant.Constants.TP_ID;
import static cn.hippo4j.common.constant.Constants.ITEM_ID;
import static cn.hippo4j.common.constant.Constants.NAMESPACE;
import static cn.hippo4j.common.constant.Constants.ACTIVE_ALARM;
import static cn.hippo4j.common.constant.Constants.CAPACITY_ALARM;
import static cn.hippo4j.common.constant.Constants.EXECUTE_TIME_OUT;
import static cn.hippo4j.common.constant.Constants.HTTP_EXECUTE_TIMEOUT;

/**
 * Dynamic thread-pool post processor.
 */
@Slf4j
@AllArgsConstructor
public final class DynamicThreadPoolPostProcessor implements BeanPostProcessor {

    private final BootstrapProperties properties;
    private final HttpAgent httpAgent;
    private final DynamicThreadPoolSubscribeConfig dynamicThreadPoolSubscribeConfig;

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
            if ((dynamicThreadPoolExecutor) == null) {
                dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) bean;
            }
            DynamicThreadPoolWrapper dynamicThreadPoolWrapper = new DynamicThreadPoolWrapper(((DynamicThreadPoolExecutor) dynamicThreadPoolExecutor).getThreadPoolId(), dynamicThreadPoolExecutor);
            ThreadPoolExecutor remoteThreadPoolExecutor = fillPoolAndRegister(dynamicThreadPoolWrapper);
            DynamicThreadPoolAdapterChoose.replace(bean, remoteThreadPoolExecutor);
            subscribeConfig(dynamicThreadPoolWrapper);
            return DynamicThreadPoolAdapterChoose.match(bean) ? bean : remoteThreadPoolExecutor;
        }
        if (bean instanceof DynamicThreadPoolWrapper) {
            DynamicThreadPoolWrapper dynamicThreadPoolWrapper = (DynamicThreadPoolWrapper) bean;
            registerAndSubscribe(dynamicThreadPoolWrapper);
        }
        return bean;
    }

    /**
     * Register and subscribe.
     *
     * @param dynamicThreadPoolWrapper dynamic thread-pool wrapper
     */
    protected void registerAndSubscribe(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        fillPoolAndRegister(dynamicThreadPoolWrapper);
        subscribeConfig(dynamicThreadPoolWrapper);
    }

    /**
     * Fill the thread pool and register.
     *
     * @param dynamicThreadPoolWrapper dynamic thread-pool wrapper
     */
    protected ThreadPoolExecutor fillPoolAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        String threadPoolId = dynamicThreadPoolWrapper.getThreadPoolId();
        ThreadPoolExecutor executor = dynamicThreadPoolWrapper.getExecutor();
        Map<String, String> queryStrMap = new HashMap<>(INITIAL_CAPACITY);
        queryStrMap.put(TP_ID, threadPoolId);
        queryStrMap.put(ITEM_ID, properties.getItemId());
        queryStrMap.put(NAMESPACE, properties.getNamespace());
        ThreadPoolParameterInfo threadPoolParameterInfo = new ThreadPoolParameterInfo();
        try {
            Result result = httpAgent.httpGetByConfig(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, HTTP_EXECUTE_TIMEOUT);
            if (result.isSuccess() && result.getData() != null) {
                String resultJsonStr = JSONUtil.toJSONString(result.getData());
                threadPoolParameterInfo = JSONUtil.parseObject(resultJsonStr, ThreadPoolParameterInfo.class);
                if (threadPoolParameterInfo != null) {
                    threadPoolParamReplace(executor, threadPoolParameterInfo);
                    registerNotifyAlarm(threadPoolParameterInfo);
                }
            } else {
                // DynamicThreadPool configuration undefined in server
                DynamicThreadPoolRegisterParameter parameterInfo = DynamicThreadPoolRegisterParameter.builder()
                        .threadPoolId(threadPoolId)
                        .corePoolSize(executor.getCorePoolSize())
                        .maximumPoolSize(executor.getMaximumPoolSize())
                        .blockingQueueType(BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(executor.getQueue().getClass().getSimpleName()))
                        .capacity(executor.getQueue().remainingCapacity())
                        .threadFactory(executor.getThreadFactory())
                        .allowCoreThreadTimeOut(executor.allowsCoreThreadTimeOut())
                        .keepAliveTime(executor.getKeepAliveTime(TimeUnit.MILLISECONDS))
                        .isAlarm(false)
                        .activeAlarm(ACTIVE_ALARM)
                        .capacityAlarm(CAPACITY_ALARM)
                        .executeTimeOut(EXECUTE_TIME_OUT)
                        .rejectedPolicyType(RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(executor.getRejectedExecutionHandler().getClass().getSimpleName()))
                        .build();
                DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                        .parameter(parameterInfo)
                        .build();
                GlobalThreadPoolManage.dynamicRegister(registerWrapper);
            }
        } catch (Exception ex) {
            log.error("Failed to initialize thread pool configuration. error message: {}", ex.getMessage());
        }
        GlobalThreadPoolManage.register(dynamicThreadPoolWrapper.getThreadPoolId(), threadPoolParameterInfo, dynamicThreadPoolWrapper);
        return executor;
    }

    /**
     * Thread-pool param replace.
     *
     * @param executor                dynamic thread-pool executor
     * @param threadPoolParameterInfo thread-pool parameter info
     */
    private void threadPoolParamReplace(ThreadPoolExecutor executor, ThreadPoolParameterInfo threadPoolParameterInfo) {
        BlockingQueue workQueue = BlockingQueueTypeEnum.createBlockingQueue(threadPoolParameterInfo.getQueueType(), threadPoolParameterInfo.getCapacity());
        ReflectUtil.setFieldValue(executor, "workQueue", workQueue);
        // fix https://github.com/opengoofy/hippo4j/issues/1063
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, threadPoolParameterInfo.corePoolSizeAdapt(), threadPoolParameterInfo.maximumPoolSizeAdapt());
        executor.setKeepAliveTime(threadPoolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(EnableEnum.getBool(threadPoolParameterInfo.getAllowCoreThreadTimeOut()));
        executor.setRejectedExecutionHandler(RejectedPolicyTypeEnum.createPolicy(threadPoolParameterInfo.getRejectedType()));
        if (executor instanceof DynamicThreadPoolExecutor) {
            Optional.ofNullable(threadPoolParameterInfo.getExecuteTimeOut())
                    .ifPresent(executeTimeOut -> ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(executeTimeOut));
        }
    }

    /**
     * Register notify alarm.
     *
     * @param threadPoolParameterInfo thread-pool parameter info
     */
    private void registerNotifyAlarm(ThreadPoolParameterInfo threadPoolParameterInfo) {
        // Set dynamic thread pool enhancement parameters.
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(
                BooleanUtil.toBoolean(threadPoolParameterInfo.getIsAlarm().toString()),
                threadPoolParameterInfo.getLivenessAlarm(),
                threadPoolParameterInfo.getCapacityAlarm());
        GlobalNotifyAlarmManage.put(threadPoolParameterInfo.getTpId(), threadPoolNotifyAlarm);
    }

    /**
     * Client dynamic thread pool subscription server configuration.
     *
     * @param dynamicThreadPoolWrapper dynamic thread-pool wrapper
     */
    protected void subscribeConfig(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        if (dynamicThreadPoolWrapper.isSubscribeFlag()) {
            dynamicThreadPoolSubscribeConfig.subscribeConfig(dynamicThreadPoolWrapper.getThreadPoolId());
        }
    }
}
