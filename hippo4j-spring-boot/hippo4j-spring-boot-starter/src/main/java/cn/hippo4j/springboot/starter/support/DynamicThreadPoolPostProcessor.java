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

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.*;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.toolkit.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.core.DynamicThreadPoolSubscribeConfig;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.task.TaskDecorator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.*;

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
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
            if ((dynamicThreadPoolExecutor = DynamicThreadPoolAdapterChoose.unwrap(bean)) == null) {
                dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) bean;
            }
            DynamicThreadPoolWrapper dynamicThreadPoolWrapper = new DynamicThreadPoolWrapper(dynamicThreadPoolExecutor.getThreadPoolId(), dynamicThreadPoolExecutor);
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
     * @param dynamicThreadPoolWrapper
     */
    protected void registerAndSubscribe(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        fillPoolAndRegister(dynamicThreadPoolWrapper);
        subscribeConfig(dynamicThreadPoolWrapper);
    }

    /**
     * Fill the thread pool and register.
     *
     * @param dynamicThreadPoolWrapper
     */
    protected ThreadPoolExecutor fillPoolAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        String threadPoolId = dynamicThreadPoolWrapper.getThreadPoolId();
        ThreadPoolExecutor executor = dynamicThreadPoolWrapper.getExecutor();
        Map<String, String> queryStrMap = new HashMap(3);
        queryStrMap.put(TP_ID, threadPoolId);
        queryStrMap.put(ITEM_ID, properties.getItemId());
        queryStrMap.put(NAMESPACE, properties.getNamespace());
        ThreadPoolExecutor newDynamicThreadPoolExecutor = null;
        ThreadPoolParameterInfo threadPoolParameterInfo = new ThreadPoolParameterInfo();
        try {
            Result result = httpAgent.httpGetByConfig(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, 5000L);
            if (result.isSuccess() && result.getData() != null) {
                String resultJsonStr = JSONUtil.toJSONString(result.getData());
                if ((threadPoolParameterInfo = JSONUtil.parseObject(resultJsonStr, ThreadPoolParameterInfo.class)) != null) {
                    // Create a thread pool with relevant parameters.
                    BlockingQueue workQueue = BlockingQueueTypeEnum.createBlockingQueue(threadPoolParameterInfo.getQueueType(), threadPoolParameterInfo.getCapacity());
                    newDynamicThreadPoolExecutor = ThreadPoolBuilder.builder()
                            .dynamicPool()
                            .threadPoolId(threadPoolId)
                            .workQueue(workQueue)
                            .threadFactory(executor.getThreadFactory())
                            .poolThreadSize(threadPoolParameterInfo.corePoolSizeAdapt(), threadPoolParameterInfo.maximumPoolSizeAdapt())
                            .keepAliveTime(threadPoolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS)
                            .rejected(RejectedPolicyTypeEnum.createPolicy(threadPoolParameterInfo.getRejectedType()))
                            .allowCoreThreadTimeOut(EnableEnum.getBool(threadPoolParameterInfo.getAllowCoreThreadTimeOut()))
                            .build();
                    // Set dynamic thread pool enhancement parameters.
                    if (executor instanceof AbstractDynamicExecutorSupport) {
                        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(
                                BooleanUtil.toBoolean(threadPoolParameterInfo.getIsAlarm().toString()),
                                threadPoolParameterInfo.getLivenessAlarm(),
                                threadPoolParameterInfo.getCapacityAlarm());
                        GlobalNotifyAlarmManage.put(threadPoolId, threadPoolNotifyAlarm);
                        TaskDecorator taskDecorator = ((DynamicThreadPoolExecutor) executor).getTaskDecorator();
                        ((DynamicThreadPoolExecutor) newDynamicThreadPoolExecutor).setTaskDecorator(taskDecorator);
                        long awaitTerminationMillis = ((DynamicThreadPoolExecutor) executor).awaitTerminationMillis;
                        boolean waitForTasksToCompleteOnShutdown = ((DynamicThreadPoolExecutor) executor).waitForTasksToCompleteOnShutdown;
                        ((DynamicThreadPoolExecutor) newDynamicThreadPoolExecutor).setSupportParam(awaitTerminationMillis, waitForTasksToCompleteOnShutdown);
                        long executeTimeOut = Optional.ofNullable(threadPoolParameterInfo.getExecuteTimeOut())
                                .orElse(((DynamicThreadPoolExecutor) executor).getExecuteTimeOut());
                        ((DynamicThreadPoolExecutor) newDynamicThreadPoolExecutor).setExecuteTimeOut(executeTimeOut);
                    }
                    dynamicThreadPoolWrapper.setExecutor(newDynamicThreadPoolExecutor);
                }
            } else {
                // DynamicThreadPool configuration undefined in server
                DynamicThreadPoolRegisterParameter parameterInfo = DynamicThreadPoolRegisterParameter.builder()
                        .threadPoolId(threadPoolId)
                        .corePoolSize(executor.getCorePoolSize())
                        .maximumPoolSize(executor.getMaximumPoolSize())
                        .blockingQueueType(BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(executor.getQueue().getClass().getSimpleName()))
                        .capacity(executor.getQueue().remainingCapacity())
                        .allowCoreThreadTimeOut(executor.allowsCoreThreadTimeOut())
                        .keepAliveTime(executor.getKeepAliveTime(TimeUnit.MILLISECONDS))
                        .isAlarm(false)
                        .activeAlarm(80)
                        .capacityAlarm(80)
                        .rejectedPolicyType(RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(((DynamicThreadPoolExecutor) executor).getRedundancyHandler().getClass().getSimpleName()))
                        .build();
                DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                        .dynamicThreadPoolRegisterParameter(parameterInfo)
                        .build();
                GlobalThreadPoolManage.dynamicRegister(registerWrapper);
            }
        } catch (Exception ex) {
            newDynamicThreadPoolExecutor = executor != null ? executor : CommonDynamicThreadPool.getInstance(threadPoolId);
            dynamicThreadPoolWrapper.setExecutor(newDynamicThreadPoolExecutor);
            log.error("Failed to initialize thread pool configuration. error message: {}", ex.getMessage());
        } finally {
            if (Objects.isNull(executor)) {
                dynamicThreadPoolWrapper.setExecutor(CommonDynamicThreadPool.getInstance(threadPoolId));
            }
        }
        GlobalThreadPoolManage.register(dynamicThreadPoolWrapper.getThreadPoolId(), threadPoolParameterInfo, dynamicThreadPoolWrapper);
        return newDynamicThreadPoolExecutor;
    }

    /**
     * Client dynamic thread pool subscription server configuration.
     *
     * @param dynamicThreadPoolWrapper
     */
    protected void subscribeConfig(DynamicThreadPoolWrapper dynamicThreadPoolWrapper) {
        if (dynamicThreadPoolWrapper.isSubscribeFlag()) {
            dynamicThreadPoolSubscribeConfig.subscribeConfig(dynamicThreadPoolWrapper.getThreadPoolId());
        }
    }
}
