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

package cn.hippo4j.threadpool.dynamic.core.executor.manage;

import cn.hippo4j.common.model.ThreadPoolParameter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Global thread-pool manage.
 */
public class GlobalThreadPoolManage {

    /**
     * Dynamic thread pool parameter container.
     */
    private static final Map<String, ThreadPoolParameter> POOL_PARAMETER = new ConcurrentHashMap();

    /**
     * Dynamic thread pool wrapper.
     */
    private static final Map<String, ThreadPoolExecutor> EXECUTOR_MAP = new ConcurrentHashMap();

    /**
     * Get the dynamic thread pool class.
     *
     * @param threadPoolId thread-pool id
     * @return dynamic thread-pool
     */
    public static ThreadPoolExecutor getExecutorService(String threadPoolId) {
        return EXECUTOR_MAP.get(threadPoolId);
    }

    /**
     * Get dynamic thread pool parameters.
     *
     * @param threadPoolId thread-pool id
     * @return thread-pool parameter
     */
    public static ThreadPoolParameter getPoolParameter(String threadPoolId) {
        return POOL_PARAMETER.get(threadPoolId);
    }

    /**
     * Register dynamic thread pool parameters.
     *
     * @param threadPoolId        thread-pool id
     * @param threadPoolParameter thread-pool parameter
     */
    public static void registerPoolParameter(String threadPoolId, ThreadPoolParameter threadPoolParameter) {
        POOL_PARAMETER.put(threadPoolId, threadPoolParameter);
    }
}
