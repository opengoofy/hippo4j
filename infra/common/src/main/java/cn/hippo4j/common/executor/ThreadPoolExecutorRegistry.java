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

package cn.hippo4j.common.executor;

import cn.hippo4j.common.model.executor.ExecutorProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadPoolExecutorRegistry {

    private static final Map<String, ThreadPoolExecutorHolder> HOLDER_MAP = new ConcurrentHashMap<>();

    public static final Map<ThreadPoolExecutor, Class<?>> REFERENCED_CLASS_MAP = new ConcurrentHashMap<>();

    public static Map<String, ThreadPoolExecutorHolder> getHolderMap() {
        return HOLDER_MAP;
    }

    public static void putHolder(String executorName, ThreadPoolExecutor executor, ExecutorProperties executorProperties) {
        ThreadPoolExecutorHolder executorHolder = new ThreadPoolExecutorHolder(executorName, executor, executorProperties);
        HOLDER_MAP.put(executorHolder.getThreadPoolId(), executorHolder);
    }

    public static void putHolder(ThreadPoolExecutorHolder executorHolder) {
        HOLDER_MAP.put(executorHolder.getThreadPoolId(), executorHolder);
    }

    public static ThreadPoolExecutorHolder getHolder(String executorName) {
        return Optional.ofNullable(HOLDER_MAP.get(executorName)).orElse(ThreadPoolExecutorHolder.EMPTY);
    }

    public static Map<ThreadPoolExecutor, Class<?>> getReferencedClassMap() {
        return REFERENCED_CLASS_MAP;
    }

    public static List<String> listThreadPoolExecutorId() {
        return new ArrayList<>(HOLDER_MAP.keySet());
    }

    /**
     * Get the number of dynamic thread pools.
     * <p> The data may be inaccurate when the project is initially
     * launched because registration is done asynchronously.
     *
     * @return thread-pool size
     */
    public static Integer getThreadPoolExecutorSize() {
        return listThreadPoolExecutorId().size();
    }
}
