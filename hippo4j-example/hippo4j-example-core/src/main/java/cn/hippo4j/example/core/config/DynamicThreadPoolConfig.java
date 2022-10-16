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

package cn.hippo4j.example.core.config;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.SpringDynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.example.core.handler.TaskTraceBuilderHandler;
import cn.hippo4j.example.core.inittest.TaskDecoratorTest;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.AVAILABLE_PROCESSORS;
import static cn.hippo4j.example.core.constant.GlobalTestConstant.MESSAGE_CONSUME;
import static cn.hippo4j.example.core.constant.GlobalTestConstant.MESSAGE_PRODUCE;

/**
 * Dynamic thread-pool config.
 */
@Slf4j
@Configuration
public class DynamicThreadPoolConfig {

    @Bean
    @DynamicThreadPool
    public Executor messageConsumeTtlDynamicThreadPool() {
        String threadPoolId = MESSAGE_CONSUME;
        ThreadPoolExecutor customExecutor = ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .executeTimeOut(800L)
                .waitForTasksToCompleteOnShutdown(true)
                .awaitTerminationMillis(5000L)
                .taskDecorator(new TaskTraceBuilderHandler())
                .build();
        // Ali ttl adaptation use case.
        Executor ttlExecutor = TtlExecutors.getTtlExecutor(customExecutor);
        return ttlExecutor;
    }

    /**
     * {@link Bean @Bean} and {@link DynamicThreadPool @DynamicThreadPool}.
     */
    @SpringDynamicThreadPool
    public ThreadPoolExecutor messageProduceDynamicThreadPool() {
        return ThreadPoolBuilder.buildDynamicPoolById(MESSAGE_PRODUCE);
    }

    /**
     * Test spring {@link ThreadPoolTaskExecutor}, Thread-pool id: testSpringThreadPoolTaskExecutor
     *
     * @return
     */
    // @Bean
    @DynamicThreadPool
    public ThreadPoolTaskExecutor testSpringThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("test-spring-task-executor_");
        int maxQueueCapacity = 200;
        threadPoolTaskExecutor.setCorePoolSize(AVAILABLE_PROCESSORS * 2);
        threadPoolTaskExecutor.setMaxPoolSize(AVAILABLE_PROCESSORS * 4);
        threadPoolTaskExecutor.setQueueCapacity(maxQueueCapacity);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setTaskDecorator(new TaskDecoratorTest.ContextCopyingDecorator());
        return threadPoolTaskExecutor;
    }
}
