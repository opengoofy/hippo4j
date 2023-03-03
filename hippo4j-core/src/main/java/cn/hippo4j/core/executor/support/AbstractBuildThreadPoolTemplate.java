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

package cn.hippo4j.core.executor.support;

import cn.hippo4j.common.design.builder.ThreadFactoryBuilder;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.*;

/**
 * Abstract build threadPool template.
 */
@Slf4j
public class AbstractBuildThreadPoolTemplate {

    /**
     * Thread-pool construction initialization parameters.
     *
     * @return thread-pool init param
     */
    protected static ThreadPoolInitParam initParam() {
        throw new UnsupportedOperationException();
    }

    /**
     * Build pool.
     *
     * @return thread-pool executor
     */
    public static ThreadPoolExecutor buildPool() {
        ThreadPoolInitParam initParam = initParam();
        return buildPool(initParam);
    }

    /**
     * Build pool.
     *
     * @param initParam init param
     * @return thread-pool executor
     */
    public static ThreadPoolExecutor buildPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        ThreadPoolExecutor executorService;
        try {
            executorService = new ThreadPoolExecutorTemplate(initParam.getCorePoolNum(),
                    initParam.getMaximumPoolSize(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    initParam.getWorkQueue(),
                    initParam.getThreadFactory(),
                    initParam.rejectedExecutionHandler);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Error creating thread pool parameter.", ex);
        }
        executorService.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return executorService;
    }

    /**
     * Build a fast-consuming task thread pool.
     *
     * @return fast thread-pool executor
     */
    public static ThreadPoolExecutor buildFastPool() {
        ThreadPoolInitParam initParam = initParam();
        return buildFastPool(initParam);
    }

    /**
     * Build a fast-consuming task thread pool.
     *
     * @param initParam init param
     * @return fast thread-pool executor
     */
    public static ThreadPoolExecutor buildFastPool(ThreadPoolInitParam initParam) {
        TaskQueue<Runnable> taskQueue = new TaskQueue(initParam.getCapacity());
        FastThreadPoolExecutor fastThreadPoolExecutor;
        try {
            fastThreadPoolExecutor = new FastThreadPoolExecutor(initParam.getCorePoolNum(),
                    initParam.getMaximumPoolSize(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    taskQueue,
                    initParam.getThreadFactory(),
                    initParam.rejectedExecutionHandler);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Error creating thread pool parameter.", ex);
        }
        taskQueue.setExecutor(fastThreadPoolExecutor);
        fastThreadPoolExecutor.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return fastThreadPoolExecutor;
    }

    /**
     * Build a dynamic monitor thread-pool.
     *
     * @param initParam init param
     * @return dynamic monitor thread-pool
     */
    public static DynamicThreadPoolExecutor buildDynamicPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
        try {
            dynamicThreadPoolExecutor = new DynamicThreadPoolExecutor(
                    initParam.getCorePoolNum(),
                    initParam.getMaximumPoolSize(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    initParam.getExecuteTimeOut(),
                    initParam.getWaitForTasksToCompleteOnShutdown(),
                    initParam.getAwaitTerminationMillis(),
                    initParam.getWorkQueue(),
                    initParam.getThreadPoolId(),
                    initParam.getThreadFactory(),
                    initParam.getRejectedExecutionHandler());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(String.format("Error creating thread pool parameter. threadPool id: %s", initParam.getThreadPoolId()), ex);
        }
        dynamicThreadPoolExecutor.setTaskDecorator(initParam.getTaskDecorator());
        dynamicThreadPoolExecutor.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return dynamicThreadPoolExecutor;
    }

    /**
     * Thread-pool init param.
     */
    @Data
    @Accessors(chain = true)
    public static class ThreadPoolInitParam {

        private Integer corePoolNum;

        private Integer maximumPoolSize;

        private Long keepAliveTime;

        private TimeUnit timeUnit;

        private Long executeTimeOut;

        private Integer capacity;

        private BlockingQueue<Runnable> workQueue;

        private RejectedExecutionHandler rejectedExecutionHandler;

        private ThreadFactory threadFactory;

        private String threadPoolId;

        private TaskDecorator taskDecorator;

        private Long awaitTerminationMillis;

        private Boolean waitForTasksToCompleteOnShutdown;

        private Boolean allowCoreThreadTimeOut = false;

        public ThreadPoolInitParam(String threadNamePrefix, boolean isDaemon) {
            this.threadFactory = ThreadFactoryBuilder.builder()
                    .prefix(threadNamePrefix)
                    .daemon(isDaemon)
                    .build();
        }

        public ThreadPoolInitParam(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
        }
    }
}
