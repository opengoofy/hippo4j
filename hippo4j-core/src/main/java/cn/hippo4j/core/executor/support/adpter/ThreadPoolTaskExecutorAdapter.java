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

package cn.hippo4j.core.executor.support.adpter;

import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Spring ThreadPoolTaskExecutor adapter.
 */
public class ThreadPoolTaskExecutorAdapter implements DynamicThreadPoolAdapter {

    private static final String EXECUTOR_FIELD_NAME = "threadPoolExecutor";

    private static final String WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = "waitForTasksToCompleteOnShutdown";

    private static final String AWAIT_TERMINATION_MILLIS = "awaitTerminationMillis";

    private static final String TASK_DECORATOR = "taskDecorator";

    private static final String BEAN_NAME = "beanName";

    private static final String QUEUE_CAPACITY = "queueCapacity";

    @Override
    public boolean match(Object executor) {
        return executor instanceof ThreadPoolTaskExecutor;
    }

    @Override
    public DynamicThreadPoolExecutor unwrap(Object executor) {
        Object unwrap = ReflectUtil.getFieldValue(executor, EXECUTOR_FIELD_NAME);
        if (unwrap == null) {
            return null;
        }
        if (!(unwrap instanceof ThreadPoolExecutor)) {
            return null;
        }
        if (unwrap instanceof DynamicThreadPoolExecutor) {
            return (DynamicThreadPoolExecutor) unwrap;
        }
        boolean waitForTasksToCompleteOnShutdown = (boolean) ReflectUtil.getFieldValue(executor, WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);
        long awaitTerminationMillis = (long) ReflectUtil.getFieldValue(executor, AWAIT_TERMINATION_MILLIS);
        String beanName = (String) ReflectUtil.getFieldValue(executor, BEAN_NAME);
        int queueCapacity = (int) ReflectUtil.getFieldValue(executor, QUEUE_CAPACITY);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) unwrap;
        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;
        // Spring ThreadPoolTaskExecutor to DynamicThreadPoolExecutor
        // ThreadPoolTaskExecutor not support executeTimeOut
        ThreadPoolBuilder threadPoolBuilder = ThreadPoolBuilder.builder()
                .dynamicPool()
                .corePoolSize(threadPoolTaskExecutor.getCorePoolSize())
                .maxPoolNum(threadPoolTaskExecutor.getMaxPoolSize())
                .keepAliveTime(threadPoolTaskExecutor.getKeepAliveSeconds())
                .timeUnit(TimeUnit.SECONDS)
                .allowCoreThreadTimeOut(threadPoolExecutor.allowsCoreThreadTimeOut())
                .waitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown)
                .awaitTerminationMillis(awaitTerminationMillis)
                .threadFactory(threadPoolExecutor.getThreadFactory())
                // threadPoolId default beanName
                .threadPoolId(beanName)
                .rejected(threadPoolExecutor.getRejectedExecutionHandler());
        // use new Queue
        threadPoolBuilder.capacity(queueCapacity);
        // .workQueue(threadPoolExecutor.getQueue())

        Optional.ofNullable(ReflectUtil.getFieldValue(executor, TASK_DECORATOR))
                .ifPresent((taskDecorator) -> threadPoolBuilder.taskDecorator((TaskDecorator) taskDecorator));

        return (DynamicThreadPoolExecutor) threadPoolBuilder.build();
    }

    @Override
    public void replace(Object executor, Executor dynamicThreadPoolExecutor) {
        ReflectUtil.setFieldValue(executor, EXECUTOR_FIELD_NAME, dynamicThreadPoolExecutor);
    }
}
