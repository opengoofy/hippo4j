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

package cn.hippo4j.core.executor.provider;

import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Common dynamic thread-pool provider factory.
 */
public class CommonDynamicThreadPoolProviderFactory {

    /**
     * Core pool size.
     */
    private static final int CORE_POOL_SIZE = 2;

    /**
     * Maximum pool size.
     */
    private static final int MAXIMUM_POOL_SIZE = 4;

    /**
     * Keep alive time.
     */
    private static final long KEEP_ALIVE_TIME = 60L;

    /**
     * Work queue capacity.
     */
    private static final int WORK_QUEUE_CAPACITY = 1024;

    /**
     * Get the public dynamic thread pool instance.
     *
     * @param threadPoolId thread-pool id
     * @return dynamic thread-pool executor
     */
    public static DynamicThreadPoolExecutor getInstance(String threadPoolId) {
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .poolThreadSize(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE)
                .keepAliveTime(KEEP_ALIVE_TIME, TimeUnit.SECONDS)
                .workQueue(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, WORK_QUEUE_CAPACITY)
                .build();
        return dynamicThreadPoolExecutor;
    }
}
