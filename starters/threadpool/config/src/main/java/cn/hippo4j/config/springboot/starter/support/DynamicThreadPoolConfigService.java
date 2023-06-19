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

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterCoreNotifyParameter;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.core.executor.support.service.AbstractDynamicThreadPoolService;
import cn.hippo4j.threadpool.message.core.service.GlobalNotifyAlarmManage;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolNotifyAlarm;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dynamic thread-pool config service.
 */
public class DynamicThreadPoolConfigService extends AbstractDynamicThreadPoolService {

    @Override
    public ThreadPoolExecutor registerDynamicThreadPool(DynamicThreadPoolRegisterWrapper registerWrapper) {
        DynamicThreadPoolRegisterParameter registerParameter = registerWrapper.getParameter();
        String threadPoolId = registerParameter.getThreadPoolId();
        ThreadPoolExecutor dynamicThreadPoolExecutor = buildDynamicThreadPoolExecutor(registerParameter);
        ExecutorProperties executorProperties = buildExecutorProperties(registerWrapper);
        ThreadPoolExecutorRegistry.putHolder(threadPoolId, dynamicThreadPoolExecutor, executorProperties);
        DynamicThreadPoolRegisterCoreNotifyParameter notifyParameter = registerWrapper.getConfigNotify();
        ThreadPoolNotifyAlarm notifyAlarm = new ThreadPoolNotifyAlarm(true, registerParameter.getActiveAlarm(), registerParameter.getCapacityAlarm());
        notifyAlarm.setReceives(notifyParameter.getReceives());
        notifyAlarm.setInterval(notifyParameter.getInterval());
        GlobalNotifyAlarmManage.put(threadPoolId, notifyAlarm);
        return dynamicThreadPoolExecutor;
    }

    private ExecutorProperties buildExecutorProperties(DynamicThreadPoolRegisterWrapper registerWrapper) {
        DynamicThreadPoolRegisterParameter registerParameter = registerWrapper.getParameter();
        ExecutorProperties executorProperties = ExecutorProperties.builder()
                .corePoolSize(registerParameter.getCorePoolSize())
                .maximumPoolSize(registerParameter.getMaximumPoolSize())
                .allowCoreThreadTimeOut(BooleanUtil.toBoolean(String.valueOf(registerParameter.getAllowCoreThreadTimeOut())))
                .keepAliveTime(registerParameter.getKeepAliveTime())
                .blockingQueue(BlockingQueueTypeEnum.getBlockingQueueNameByType(registerParameter.getBlockingQueueType().getType()))
                .queueCapacity(registerParameter.getCapacity())
                .threadNamePrefix(registerParameter.getThreadNamePrefix())
                .rejectedHandler(RejectedPolicyTypeEnum.getRejectedNameByType(registerParameter.getRejectedPolicyType().getType()))
                .executeTimeOut(registerParameter.getExecuteTimeOut())
                .threadPoolId(registerParameter.getThreadPoolId())
                .build();
        return executorProperties;
    }
}
