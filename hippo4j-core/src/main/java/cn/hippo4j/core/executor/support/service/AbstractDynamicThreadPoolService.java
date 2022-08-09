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

package cn.hippo4j.core.executor.support.service;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.RejectedTypeEnum;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract dynamic thread-pool service.
 */
public abstract class AbstractDynamicThreadPoolService implements DynamicThreadPoolService {

    /**
     * Build dynamic thread-pool executor.
     *
     * @param registerParameter
     * @return
     */
    public ThreadPoolExecutor buildDynamicThreadPoolExecutor(DynamicThreadPoolRegisterParameter registerParameter) {
        ThreadPoolExecutor dynamicThreadPoolExecutor = ThreadPoolBuilder.builder()
                .threadPoolId(registerParameter.getThreadPoolId())
                .corePoolSize(registerParameter.getCorePoolSize())
                .maxPoolNum(registerParameter.getMaximumPoolSize())
                .workQueue(QueueTypeEnum.createBlockingQueue(registerParameter.getQueueType(), registerParameter.getCapacity()))
                .threadFactory(registerParameter.getThreadNamePrefix())
                .keepAliveTime(registerParameter.getKeepAliveTime())
                .rejected(RejectedTypeEnum.createPolicy(registerParameter.getRejectedType()))
                .dynamicPool()
                .build();
        return dynamicThreadPoolExecutor;
    }
}
