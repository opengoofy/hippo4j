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

package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.*;
import cn.hippo4j.core.toolkit.inet.DynamicThreadPoolAnnotationUtil;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import cn.hutool.core.util.BooleanUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.task.TaskDecorator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
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

    private final ThreadPoolOperation threadPoolOperation;

    private final ServerThreadPoolDynamicRefresh threadPoolDynamicRefresh;

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
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) bean;
            DynamicThreadPoolWrapper wrap = new DynamicThreadPoolWrapper(dynamicThreadPoolExecutor.getThreadPoolId(), dynamicThreadPoolExecutor);
            ThreadPoolExecutor remoteExecutor = fillPoolAndRegister(wrap);
            subscribeConfig(wrap);
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
        subscribeConfig(dynamicThreadPoolWrap);
    }

    /**
     * Fill the thread pool and register.
     *
     * @param dynamicThreadPoolWrap
     */
    protected ThreadPoolExecutor fillPoolAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        String tpId = dynamicThreadPoolWrap.getThreadPoolId();
        Map<String, String> queryStrMap = new HashMap(3);
        queryStrMap.put(TP_ID, tpId);
        queryStrMap.put(ITEM_ID, properties.getItemId());
        queryStrMap.put(NAMESPACE, properties.getNamespace());
        boolean isSubscribe = false;
        ThreadPoolExecutor newDynamicThreadPoolExecutor = null;
        ThreadPoolParameterInfo threadPoolParameterInfo = new ThreadPoolParameterInfo();
        try {
            Result result = httpAgent.httpGetByConfig(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, 5000L);
            if (result.isSuccess() && result.getData() != null) {
                String resultJsonStr = JSONUtil.toJSONString(result.getData());
                if ((threadPoolParameterInfo = JSONUtil.parseObject(resultJsonStr, ThreadPoolParameterInfo.class)) != null) {
                    // Create a thread pool with relevant parameters.
                    BlockingQueue workQueue = QueueTypeEnum.createBlockingQueue(threadPoolParameterInfo.getQueueType(), threadPoolParameterInfo.getCapacity());
                    newDynamicThreadPoolExecutor = ThreadPoolBuilder.builder()
                            .dynamicPool()
                            .workQueue(workQueue)
                            .threadFactory(tpId)
                            .poolThreadSize(threadPoolParameterInfo.corePoolSizeAdapt(), threadPoolParameterInfo.maximumPoolSizeAdapt())
                            .keepAliveTime(threadPoolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS)
                            .rejected(RejectedTypeEnum.createPolicy(threadPoolParameterInfo.getRejectedType()))
                            .allowCoreThreadTimeOut(EnableEnum.getBool(threadPoolParameterInfo.getAllowCoreThreadTimeOut()))
                            .build();
                    // Set dynamic thread pool enhancement parameters.
                    if (dynamicThreadPoolWrap.getExecutor() instanceof AbstractDynamicExecutorSupport) {
                        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(
                                BooleanUtil.toBoolean(threadPoolParameterInfo.getIsAlarm().toString()),
                                threadPoolParameterInfo.getCapacityAlarm(),
                                threadPoolParameterInfo.getLivenessAlarm());
                        GlobalNotifyAlarmManage.put(tpId, threadPoolNotifyAlarm);
                        TaskDecorator taskDecorator = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).getTaskDecorator();
                        ((DynamicThreadPoolExecutor) newDynamicThreadPoolExecutor).setTaskDecorator(taskDecorator);
                        long awaitTerminationMillis = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).awaitTerminationMillis;
                        boolean waitForTasksToCompleteOnShutdown = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).waitForTasksToCompleteOnShutdown;
                        ((DynamicThreadPoolExecutor) newDynamicThreadPoolExecutor).setSupportParam(awaitTerminationMillis, waitForTasksToCompleteOnShutdown);
                        long executeTimeOut = ((DynamicThreadPoolExecutor) dynamicThreadPoolWrap.getExecutor()).getExecuteTimeOut();
                        ((DynamicThreadPoolExecutor) newDynamicThreadPoolExecutor).setExecuteTimeOut(executeTimeOut);
                    }
                    dynamicThreadPoolWrap.setExecutor(newDynamicThreadPoolExecutor);
                    isSubscribe = true;
                }
            }
        } catch (Exception ex) {
            newDynamicThreadPoolExecutor = dynamicThreadPoolWrap.getExecutor() != null ? dynamicThreadPoolWrap.getExecutor() : CommonDynamicThreadPool.getInstance(tpId);
            dynamicThreadPoolWrap.setExecutor(newDynamicThreadPoolExecutor);
            log.error("Failed to initialize thread pool configuration. error message :: {}", ex.getMessage());
        } finally {
            if (Objects.isNull(dynamicThreadPoolWrap.getExecutor())) {
                dynamicThreadPoolWrap.setExecutor(CommonDynamicThreadPool.getInstance(tpId));
            }
            // Set whether to subscribe to the remote thread pool configuration.
            dynamicThreadPoolWrap.setSubscribeFlag(isSubscribe);
        }
        GlobalThreadPoolManage.register(dynamicThreadPoolWrap.getThreadPoolId(), threadPoolParameterInfo, dynamicThreadPoolWrap);
        return newDynamicThreadPoolExecutor;
    }

    private final ExecutorService configRefreshExecutorService = ThreadPoolBuilder.builder()
            .corePoolSize(2)
            .maxPoolNum(4)
            .keepAliveTime(2000)
            .timeUnit(TimeUnit.MILLISECONDS)
            .workQueue(QueueTypeEnum.ARRAY_BLOCKING_QUEUE)
            .capacity(1024)
            .allowCoreThreadTimeOut(true)
            .threadFactory("client.dynamic.threadPool.change.config")
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    /**
     * Client dynamic thread pool subscription server configuration.
     *
     * @param dynamicThreadPoolWrap
     */
    protected void subscribeConfig(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        if (dynamicThreadPoolWrap.isSubscribeFlag()) {
            threadPoolOperation.subscribeConfig(dynamicThreadPoolWrap.getThreadPoolId(), configRefreshExecutorService, config -> threadPoolDynamicRefresh.dynamicRefresh(config));
        }
    }
}
