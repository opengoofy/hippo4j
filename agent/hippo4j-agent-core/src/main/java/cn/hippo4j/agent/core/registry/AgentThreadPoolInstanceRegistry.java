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

package cn.hippo4j.agent.core.registry;

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class AgentThreadPoolInstanceRegistry {

    private static final ILog LOGGER = LogManager.getLogger(AgentThreadPoolInstanceRegistry.class);

    private final Map<String, AgentThreadPoolExecutorHolder> holderMap = new ConcurrentHashMap<>();

    public final Map<ThreadPoolExecutor, Class<?>> earlyConstructMap = new ConcurrentHashMap<>();

    private volatile static AgentThreadPoolInstanceRegistry INSTANCE;

    private AgentThreadPoolInstanceRegistry() {
    }

    public static AgentThreadPoolInstanceRegistry getInstance() {
        if (INSTANCE == null) {
            synchronized (AgentThreadPoolInstanceRegistry.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AgentThreadPoolInstanceRegistry();
                }
            }
        }
        return INSTANCE;
    }

    public Map<String, AgentThreadPoolExecutorHolder> getHolderMap() {
        return holderMap;
    }

    public void putHolder(String executorName, ThreadPoolExecutor executor, Properties properties) {
        AgentThreadPoolExecutorHolder holder = new AgentThreadPoolExecutorHolder(executorName, executor, properties);
        holderMap.put(executorName, holder);
    }

    public AgentThreadPoolExecutorHolder getHolder(String executorName) {
        return Optional.ofNullable(holderMap.get(executorName)).orElse(AgentThreadPoolExecutorHolder.EMPTY);
    }
}
