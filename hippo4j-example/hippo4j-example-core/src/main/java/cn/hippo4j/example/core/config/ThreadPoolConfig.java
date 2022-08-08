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
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.example.core.handler.TaskTraceBuilderHandler;
import cn.hippo4j.example.core.inittest.TaskDecoratorTest;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.example.core.constant.GlobalTestConstant.MESSAGE_CONSUME;
import static cn.hippo4j.example.core.constant.GlobalTestConstant.MESSAGE_PRODUCE;

/**
 * Thread pool config.
 *
 * @author chen.ma
 * @date 2021/6/20 17:16
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor messageConsumeDynamicThreadPool() {
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
        return customExecutor;
    }

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor messageProduceDynamicThreadPool() {
        String threadPoolId = MESSAGE_PRODUCE;
        ThreadPoolExecutor produceExecutor = ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .executeTimeOut(900L)
                .waitForTasksToCompleteOnShutdown(true)
                .awaitTerminationMillis(5000L)
                /**
                 * 上下文传递，测试用例：{@link TaskDecoratorTest}
                 */
                .taskDecorator(new TaskDecoratorTest.ContextCopyingDecorator())
                .build();
        return produceExecutor;
    }

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
        Executor ttlExecutor = TtlExecutors.getTtlExecutor(customExecutor);
        return ttlExecutor;
    }

    @Bean
    @DynamicThreadPool
    public Executor messageConsumeTtlServiceDynamicThreadPool() {
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
        Executor ttlExecutor = TtlExecutors.getTtlExecutorService(customExecutor);
        return ttlExecutor;
    }
}
