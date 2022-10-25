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

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.core.plugin.DefaultThreadPoolPluginManager;
import cn.hippo4j.core.plugin.DefaultThreadPoolPluginRegistrar;
import cn.hippo4j.core.plugin.impl.TaskDecoratorPlugin;
import cn.hippo4j.core.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.plugin.impl.TaskTimeoutNotifyAlarmPlugin;
import cn.hippo4j.core.plugin.impl.ThreadPoolExecutorShutdownPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced dynamic and monitored thread pool.
 */
@Slf4j
public class DynamicThreadPoolExecutor extends ExtensibleThreadPoolExecutor implements DisposableBean {

    /**
     * Creates a new {@code DynamicThreadPoolExecutor} with the given initial parameters.
     *
     * @param threadPoolId    thread-pool id
     * @param executeTimeOut  execute time out
     * @param waitForTasksToCompleteOnShutdown wait for tasks to complete on shutdown
     * @param awaitTerminationMillis await termination millis
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param blockingQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor creates a new thread
     * @param rejectedExecutionHandler the handler to use when execution is blocked because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code threadFactory} or {@code handler} is null
     */
    public DynamicThreadPoolExecutor(
        int corePoolSize, int maximumPoolSize,
        long keepAliveTime, TimeUnit unit,
        long executeTimeOut, boolean waitForTasksToCompleteOnShutdown, long awaitTerminationMillis,
        @NonNull BlockingQueue<Runnable> blockingQueue,
        @NonNull String threadPoolId,
        @NonNull ThreadFactory threadFactory,
        @NonNull RejectedExecutionHandler rejectedExecutionHandler) {
        super(
            threadPoolId, new DefaultThreadPoolPluginManager(),
            corePoolSize, maximumPoolSize, keepAliveTime, unit,
            blockingQueue, threadFactory, rejectedExecutionHandler
        );
        log.info("Initializing ExecutorService" + threadPoolId);

        // init default aware processor
        new DefaultThreadPoolPluginRegistrar(executeTimeOut, awaitTerminationMillis, waitForTasksToCompleteOnShutdown)
            .doRegister(this, this);
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     */
    @Override
    public void destroy() {
        if (isWaitForTasksToCompleteOnShutdown()) {
            super.shutdown();
        } else {
            super.shutdownNow();
        }
    }

    public long getAwaitTerminationMillis() {
        return getPluginOfType(ThreadPoolExecutorShutdownPlugin.PLUGIN_NAME, ThreadPoolExecutorShutdownPlugin.class)
            .map(ThreadPoolExecutorShutdownPlugin::getAwaitTerminationMillis)
            .orElse(-1L);
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return getPluginOfType(ThreadPoolExecutorShutdownPlugin.PLUGIN_NAME, ThreadPoolExecutorShutdownPlugin.class)
            .map(ThreadPoolExecutorShutdownPlugin::isWaitForTasksToCompleteOnShutdown)
            .orElse(false);
    }

    /**
     * Set support param.
     *
     * @param awaitTerminationMillis await termination millis
     * @param waitForTasksToCompleteOnShutdown wait for tasks to complete on shutdown
     */
    public void setSupportParam(long awaitTerminationMillis, boolean waitForTasksToCompleteOnShutdown) {
        getPluginOfType(ThreadPoolExecutorShutdownPlugin.PLUGIN_NAME, ThreadPoolExecutorShutdownPlugin.class)
            .ifPresent(processor -> processor
                .setAwaitTerminationMillis(awaitTerminationMillis)
                .setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown)
            );
    }

    public Long getRejectCountNum() {
        return getPluginOfType(TaskRejectCountRecordPlugin.PLUGIN_NAME, TaskRejectCountRecordPlugin.class)
            .map(TaskRejectCountRecordPlugin::getRejectCountNum)
            .orElse(-1L);
    }

    public Long getExecuteTimeOut() {
        return getPluginOfType(TaskTimeoutNotifyAlarmPlugin.PLUGIN_NAME, TaskTimeoutNotifyAlarmPlugin.class)
            .map(TaskTimeoutNotifyAlarmPlugin::getExecuteTimeOut)
            .orElse(-1L);
    }

    public void setExecuteTimeOut(Long executeTimeOut) {
        getPluginOfType(TaskTimeoutNotifyAlarmPlugin.PLUGIN_NAME, TaskTimeoutNotifyAlarmPlugin.class)
            .ifPresent(processor -> processor.setExecuteTimeOut(executeTimeOut));
    }

    public TaskDecorator getTaskDecorator() {
        return getPluginOfType(TaskDecoratorPlugin.PLUGIN_NAME, TaskDecoratorPlugin.class)
            .map(processor -> CollectionUtil.getFirst(processor.getDecorators()))
            .orElse(null);
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        getPluginOfType(TaskDecoratorPlugin.PLUGIN_NAME, TaskDecoratorPlugin.class)
            .ifPresent(processor -> {
                processor.clearDecorators();
                processor.addDecorator(taskDecorator);
            });
    }

    public RejectedExecutionHandler getRedundancyHandler() {
        return getRejectedExecutionHandler();
    }

    public void getRedundancyHandler(RejectedExecutionHandler handler) {
        setRejectedExecutionHandler(handler);
    }

}
