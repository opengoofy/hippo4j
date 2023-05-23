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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadPoolInstanceRegistry {

    private final Map<String, ThreadPoolExecutorHolder> holderMap = new ConcurrentHashMap<>();

    public final Map<ThreadPoolExecutor, Class<?>> earlyConstructMap = new ConcurrentHashMap<>();

    private volatile static ThreadPoolInstanceRegistry INSTANCE;

    public static ThreadPoolInstanceRegistry getInstance() {
        if (INSTANCE == null) {
            synchronized (ThreadPoolInstanceRegistry.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadPoolInstanceRegistry();
                }
            }
        }
        return INSTANCE;
    }

    public Map<String, ThreadPoolExecutorHolder> getHolderMap() {
        return holderMap;
    }

    public void putHolder(String executorName, ThreadPoolExecutor executor, ExecutorProperties executorProperties) {
        ThreadPoolExecutorHolder holder = new ThreadPoolExecutorHolder(executorName, executor, executorProperties);
        holderMap.put(executorName, holder);
    }

    public ThreadPoolExecutorHolder getHolder(String executorName) {
        return Optional.ofNullable(holderMap.get(executorName)).orElse(ThreadPoolExecutorHolder.EMPTY);
    }
}
