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

package cn.hippo4j.example.core.inittest;

import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterCoreNotifyParameter;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterServerNotifyParameter;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.threadpool.message.api.NotifyPlatformEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Register dynamic thread-pool test.
 */
@Slf4j
public class RegisterDynamicThreadPoolTest {

    private static final int CAPACITY = 1024;
    private static final long KEEP_ALIVE_TIME = 1024L;
    private static final long EXECUTE_TIMEOUT = 1024L;
    private static final int CAPACITY_ALARM = 90;
    private static final int ACTIVE_ALARM = 90;
    private static final int CORE_NOTIFY_INTERVAL = 5;
    private static final int SERVER_NOTIFY_INTERVAL = 10;

    public static ThreadPoolExecutor registerDynamicThreadPool(String threadPoolId) {
        DynamicThreadPoolRegisterParameter parameterInfo = DynamicThreadPoolRegisterParameter.builder()
                .corePoolSize(1)
                .maximumPoolSize(2)
                .blockingQueueType(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE)
                .capacity(CAPACITY)
                // TimeUnit.SECONDS
                .keepAliveTime(KEEP_ALIVE_TIME)
                // TimeUnit.MILLISECONDS
                .executeTimeOut(EXECUTE_TIMEOUT)
                .rejectedPolicyType(RejectedPolicyTypeEnum.DISCARD_POLICY)
                .isAlarm(true)
                .allowCoreThreadTimeOut(false)
                .capacityAlarm(CAPACITY_ALARM)
                .activeAlarm(ACTIVE_ALARM)
                .threadPoolId(threadPoolId)
                .threadNamePrefix(threadPoolId)
                .build();
        // Core mode and server mode, you can choose one of them.
        DynamicThreadPoolRegisterCoreNotifyParameter coreNotifyParameter = DynamicThreadPoolRegisterCoreNotifyParameter.builder()
                .receives("chen.ma")
                .interval(CORE_NOTIFY_INTERVAL)
                .build();
        DynamicThreadPoolRegisterServerNotifyParameter serverNotifyParameter = DynamicThreadPoolRegisterServerNotifyParameter.builder()
                .platform(NotifyPlatformEnum.WECHAT.name())
                .accessToken("7487d0a0-20ec-40ab-b67b-ce68db406b37")
                .interval(SERVER_NOTIFY_INTERVAL)
                .receives("chen.ma")
                .build();
        DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                .updateIfExists(true)
                .notifyUpdateIfExists(true)
                .parameter(parameterInfo)
                .configNotify(coreNotifyParameter)
                .serverNotify(serverNotifyParameter)
                .build();
        ThreadPoolExecutor dynamicThreadPool = GlobalThreadPoolManage.dynamicRegister(registerWrapper);
        log.info("Dynamic registration thread pool parameter details: {}", dynamicThreadPool);
        return dynamicThreadPool;
    }
}
