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

package cn.hippo4j.core.executor;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.support.AbstractDynamicExecutorSupport;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import cn.hippo4j.core.toolkit.SystemClock;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Enhanced dynamic and monitored thread pool.
 */
public class DynamicThreadPoolExecutor extends AbstractDynamicExecutorSupport {

    @Getter
    @Setter
    private Long executeTimeOut;

    @Getter
    @Setter
    private TaskDecorator taskDecorator;

    @Getter
    @Setter
    private RejectedExecutionHandler redundancyHandler;

    @Getter
    private final String threadPoolId;

    @Getter
    private final AtomicLong rejectCount = new AtomicLong();

    private final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();

    public DynamicThreadPoolExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit unit,
                                     long executeTimeOut,
                                     boolean waitForTasksToCompleteOnShutdown,
                                     long awaitTerminationMillis,
                                     @NonNull BlockingQueue<Runnable> blockingQueue,
                                     @NonNull String threadPoolId,
                                     @NonNull ThreadFactory threadFactory,
                                     @NonNull RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, waitForTasksToCompleteOnShutdown, awaitTerminationMillis, blockingQueue, threadPoolId, threadFactory, rejectedExecutionHandler);
        this.threadPoolId = threadPoolId;
        this.executeTimeOut = executeTimeOut;
        // Number of dynamic proxy denial policies.
        RejectedExecutionHandler rejectedProxy = RejectedProxyUtil.createProxy(rejectedExecutionHandler, threadPoolId, rejectCount);
        setRejectedExecutionHandler(rejectedProxy);
        // Redundant fields to avoid reflecting the acquired fields when sending change information.
        redundancyHandler = rejectedExecutionHandler;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (taskDecorator != null) {
            command = taskDecorator.decorate(command);
        }
        super.execute(command);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (executeTimeOut == null || executeTimeOut <= 0) {
            return;
        }
        startTimeThreadLocal.set(SystemClock.now());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Long startTime;
        if ((startTime = startTimeThreadLocal.get()) == null) {
            return;
        }
        try {
            long endTime = SystemClock.now();
            long executeTime;
            boolean executeTimeAlarm = (executeTime = (endTime - startTime)) > executeTimeOut;
            if (executeTimeAlarm && ApplicationContextHolder.getInstance() != null) {
                ThreadPoolNotifyAlarmHandler notifyAlarmHandler = ApplicationContextHolder.getBean(ThreadPoolNotifyAlarmHandler.class);
                if (notifyAlarmHandler != null) {
                    notifyAlarmHandler.asyncSendExecuteTimeOutAlarm(threadPoolId, executeTime, executeTimeOut, this);
                }
            }
        } finally {
            startTimeThreadLocal.remove();
        }
    }

    @Override
    protected ExecutorService initializeExecutor() {
        return this;
    }

    public Long getRejectCountNum() {
        return rejectCount.get();
    }
}
