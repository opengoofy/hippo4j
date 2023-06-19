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

package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic thread-pool subscribe config.
 */
@RequiredArgsConstructor
public class DynamicThreadPoolSubscribeConfig {

    private final ThreadPoolDynamicRefresh threadPoolDynamicRefresh;

    private final ClientWorker clientWorker;

    private final BootstrapProperties properties;

    private final int defaultAliveTime = 2000;

    private final ExecutorService configRefreshExecutorService = ThreadPoolBuilder.builder()
            .corePoolSize(1)
            .maximumPoolSize(2)
            .keepAliveTime(defaultAliveTime)
            .timeUnit(TimeUnit.MILLISECONDS)
            .workQueue(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
            .allowCoreThreadTimeOut(true)
            .threadFactory("client.dynamic.threadPool.change.config")
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    public void subscribeConfig(String threadPoolId) {
        subscribeConfig(threadPoolId, threadPoolDynamicRefresh::dynamicRefresh);
    }

    public void subscribeConfig(String threadPoolId, ThreadPoolSubscribeCallback threadPoolSubscribeCallback) {
        Listener configListener = new Listener() {

            @Override
            public void receiveConfigInfo(String config) {
                threadPoolSubscribeCallback.callback(config);
            }

            @Override
            public Executor getExecutor() {
                return configRefreshExecutorService;
            }
        };
        clientWorker.addTenantListeners(properties.getNamespace(), properties.getItemId(), threadPoolId, Arrays.asList(configListener));
    }
}
