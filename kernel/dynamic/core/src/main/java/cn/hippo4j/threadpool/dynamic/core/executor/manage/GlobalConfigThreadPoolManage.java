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

import cn.hippo4j.common.model.executor.ExecutorProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global core thread-pool manage.
 * TODO
 */
public class GlobalConfigThreadPoolManage {

    private static final Map<String, ExecutorProperties> EXECUTOR_PROPERTIES = new ConcurrentHashMap<>();

    /**
     * Get properties.
     *
     * @param threadPoolId
     * @return
     */
    public static ExecutorProperties getProperties(String threadPoolId) {
        return EXECUTOR_PROPERTIES.get(threadPoolId);
    }

    /**
     * Register.
     *
     * @param threadPoolId
     * @param executorProperties
     */
    public static void register(String threadPoolId, ExecutorProperties executorProperties) {
        EXECUTOR_PROPERTIES.put(threadPoolId, executorProperties);
    }

    /**
     * Refresh.
     *
     * @param threadPoolId
     * @param executorProperties
     */
    public static void refresh(String threadPoolId, ExecutorProperties executorProperties) {
        EXECUTOR_PROPERTIES.put(threadPoolId, executorProperties);
    }
}
