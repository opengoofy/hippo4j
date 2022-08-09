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

package cn.hippo4j.core.executor.manage;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.support.DynamicThreadPoolService;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private static final Map<String, DynamicThreadPoolWrapper> EXECUTOR_MAP = new ConcurrentHashMap();

    /**
     * Get the dynamic thread pool wrapper class.
     *
     * @param threadPoolId
     * @return
     */
    public static DynamicThreadPoolWrapper getExecutorService(String threadPoolId) {
        return EXECUTOR_MAP.get(threadPoolId);
    }

    /**
     * Get the dynamic thread pool wrapper class.
     *
     * @param threadPoolId
     * @return
     */
    public static ThreadPoolExecutor getExecutor(String threadPoolId) {
        return Optional.ofNullable(EXECUTOR_MAP.get(threadPoolId)).map(each -> each.getExecutor()).orElse(null);
    }

    /**
     * Get dynamic thread pool parameters.
     *
     * @param threadPoolId
     * @return
     */
    public static ThreadPoolParameter getPoolParameter(String threadPoolId) {
        return POOL_PARAMETER.get(threadPoolId);
    }

    /**
     * Register dynamic thread pool wrapper and parameters.
     *
     * @param threadPoolId
     * @param threadPoolParameter
     * @param executor
     */
    public static void register(String threadPoolId, ThreadPoolParameter threadPoolParameter, DynamicThreadPoolWrapper executor) {
        registerPool(threadPoolId, executor);
        registerPoolParameter(threadPoolId, threadPoolParameter);
    }

    /**
     * Register dynamic thread pool.
     *
     * @param threadPoolId
     * @param executor
     */
    public static void registerPool(String threadPoolId, DynamicThreadPoolWrapper executor) {
        EXECUTOR_MAP.put(threadPoolId, executor);
    }

    /**
     * Register dynamic thread pool parameters.
     *
     * @param threadPoolId
     * @param poolParameter
     */
    public static void registerPoolParameter(String threadPoolId, ThreadPoolParameter poolParameter) {
        POOL_PARAMETER.put(threadPoolId, poolParameter);
    }

    /**
     * Dynamically register thread pool records and notification records.
     *
     * @param registerWrapper
     */
    public static ThreadPoolExecutor dynamicRegister(DynamicThreadPoolRegisterWrapper registerWrapper) {
        DynamicThreadPoolService dynamicThreadPoolService = ApplicationContextHolder.getBean(DynamicThreadPoolService.class);
        return dynamicThreadPoolService.registerDynamicThreadPool(registerWrapper);
    }

    /**
     * Get the dynamic thread pool identifier collection.
     *
     * @return
     */
    public static List<String> listThreadPoolId() {
        return Lists.newArrayList(EXECUTOR_MAP.keySet());
    }

    /**
     * Get the number of dynamic thread pools.
     * <p>
     * The data may be inaccurate when the project is initially
     * launched because registration is done asynchronously.
     *
     * @return
     */
    public static Integer getThreadPoolNum() {
        return listThreadPoolId().size();
    }
}
