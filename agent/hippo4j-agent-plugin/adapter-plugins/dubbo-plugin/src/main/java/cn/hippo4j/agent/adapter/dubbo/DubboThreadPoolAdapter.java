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

package cn.hippo4j.agent.adapter.dubbo;

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;

/**
 * Dubbo thread-pool adapter.
 */
public class DubboThreadPoolAdapter {

    public static void registerExecutors() {
        boolean isLegacyVersion = true;
        String poolKey = ExecutorService.class.getName();
        // Since 2.7.5, Dubbo has changed the way thread pools are used
        // fixed https://github.com/opengoofy/hippo4j/issues/708
        try {
            if (Version.getIntVersion(Version.getVersion()) < 2070500) {
                isLegacyVersion = false;
            }
        } catch (Exception ex) {
        }

        try {
            if (isLegacyVersion) {
                DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
                Map<String, Object> executors = dataStore.get(poolKey);
                executors.forEach((key, value) -> putHolder(mark() + key, (ThreadPoolExecutor) value));
                return;
            }
            ExecutorRepository executorRepository = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
            ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>> data =
                    (ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>>) ReflectUtil.getFieldValue(executorRepository, "data");
            ConcurrentMap<Integer, ExecutorService> executorServiceMap = data.get(poolKey);
            executorServiceMap.forEach((key, value) -> putHolder(mark() + key, (ThreadPoolExecutor) value));
        } catch (Exception ex) {
        }
    }

    private static void putHolder(String executorName, ThreadPoolExecutor executor) {
        if (executor == null) {
            return;
        }
        ExecutorProperties executorProperties = ExecutorProperties.builder()
                .threadPoolId(executorName)
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .allowCoreThreadTimeOut(BooleanUtil.toBoolean(String.valueOf(executor.allowsCoreThreadTimeOut())))
                .blockingQueue(BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(executor.getQueue().getClass().getSimpleName()).getName())
                .queueCapacity(executor.getQueue().remainingCapacity())
                .rejectedHandler(RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(executor.getRejectedExecutionHandler().getClass().getSimpleName()).getName())
                .build();
        ThreadPoolExecutorRegistry.putHolder(executorName, executor, executorProperties);
    }

    public static String mark() {
        return "Dubbo";
    }
}
