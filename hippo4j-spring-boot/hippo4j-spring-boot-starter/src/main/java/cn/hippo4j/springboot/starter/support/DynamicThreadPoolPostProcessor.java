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
import static cn.hippo4j.common.constant.Constants.*;
import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapter;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.core.toolkit.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.message.service.GlobalNotifyAlarmManage;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.core.DynamicThreadPoolSubscribeConfig;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

import java.util.HashMap;
import java.util.Map;
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
public final class DynamicThreadPoolPostProcessor implements BeanPostProcessor, DestructionAwareBeanPostProcessor {

    /**
     * Properties.
     */
    private final BootstrapProperties properties;

    /**
     * Http agent.
     */
    private final HttpAgent httpAgent;

    /**
     * Dynamic thread pool subscribe config.
     */
    private final DynamicThreadPoolSubscribeConfig dynamicThreadPoolSubscribeConfig;

    /**
     * Adapted beans.
     */
    private final Map<String, String> adaptedBeans = new HashMap<>();

    /**
     * Post process before initialization
     *
     * @param bean     the bean instance to be process
     * @param beanName the name of the bean
     * @return bean
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * <p>Post process the following beans:
     * <ul>
     *     <li>
     *         instance of {@link DynamicThreadPoolExecutor},
     *         and the class or factory method is annotated by {@link DynamicThreadPool};
     *     </li>
     *     <li>
     *         matched with any {@link DynamicThreadPoolAdapter},
     *         and the class or factory method is annotated by {@link DynamicThreadPool};
     *     </li>
     *     <li>instance of {@link DynamicThreadPoolWrapper};</li>
     * </ul>
     * After finishing post-processing, the above beans will refresh their own parameters according to the configuration,
     * complete {@link ThreadPoolNotifyAlarm} configuration, and be registered to {@link GlobalThreadPoolManage}.
     *
     * @param bean     the bean instance to be process
     * @param beanName the name of the bean
     * @return bean
     * @see DynamicThreadPoolAdapterChoose#match
     * @see DynamicThreadPoolAdapterChoose#replace 
     * @see GlobalThreadPoolManage#dynamicRegister
     * @see GlobalNotifyAlarmManage#put
     * @see DynamicThreadPoolSubscribeConfig#subscribeConfig(String) 
     */
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

            // If there is an internal thread pool that is not managed by spring,`
            // the bean name of its external bean will be recorded.
            // When the external bean is destroyed,
            // the destroy method of the internal thread pool will be called actively
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor = DynamicThreadPoolAdapterChoose.unwrap(bean);
            if (Objects.nonNull(dynamicThreadPoolExecutor)) {
                if (log.isDebugEnabled()) {
                    log.debug("Adapt thread pool executor bean [{}]", beanName);
                }
                adaptedBeans.put(beanName, dynamicThreadPoolExecutor.getThreadPoolId());
            } else {
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
     * After the executor is destroyed, the callback of its internal executor will be triggered.
     *
     * @param bean     the bean instance to be destroyed
     * @param beanName the name of the bean
     * @see DynamicThreadPoolExecutor#destroy()
     */
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        Optional.ofNullable(adaptedBeans.get(beanName))
                // the internal thread pool is also managed by spring, no manual destruction required
                .filter(id -> !ApplicationContextHolder.getInstance().containsBeanDefinition(id))
                .map(GlobalThreadPoolManage::getExecutorService)
                .ifPresent(executor -> {
                    try {
                        executor.destroy();
                    } catch (Exception e) {
                        log.warn("Failed to destroy dynamic thread pool '{}'", beanName);
                    }
                });

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
        Map<String, String> queryStrMap = new HashMap(3);
        queryStrMap.put(TP_ID, threadPoolId);
        queryStrMap.put(ITEM_ID, properties.getItemId());
        queryStrMap.put(NAMESPACE, properties.getNamespace());
        ThreadPoolParameterInfo threadPoolParameterInfo = new ThreadPoolParameterInfo();
        try {
            Result result = httpAgent.httpGetByConfig(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, 5000L);
            if (result.isSuccess() && result.getData() != null) {
                String resultJsonStr = JSONUtil.toJSONString(result.getData());
                if ((threadPoolParameterInfo = JSONUtil.parseObject(resultJsonStr, ThreadPoolParameterInfo.class)) != null) {
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
                        .activeAlarm(80)
                        .capacityAlarm(80)
                        .executeTimeOut(10000L)
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
        BlockingQueue<?> workQueue = BlockingQueueTypeEnum.createBlockingQueue(threadPoolParameterInfo.getQueueType(), threadPoolParameterInfo.getCapacity());
        ReflectUtil.setFieldValue(executor, "workQueue", workQueue);
        executor.setCorePoolSize(threadPoolParameterInfo.corePoolSizeAdapt());
        executor.setMaximumPoolSize(threadPoolParameterInfo.maximumPoolSizeAdapt());
        executor.setKeepAliveTime(threadPoolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(EnableEnum.getBool(threadPoolParameterInfo.getAllowCoreThreadTimeOut()));
        executor.setRejectedExecutionHandler(RejectedPolicyTypeEnum.createPolicy(threadPoolParameterInfo.getRejectedType()));
        if (executor instanceof DynamicThreadPoolExecutor) {
            Optional.ofNullable(threadPoolParameterInfo.getExecuteTimeOut())
                    .ifPresent(((DynamicThreadPoolExecutor) executor)::setExecuteTimeOut);
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
