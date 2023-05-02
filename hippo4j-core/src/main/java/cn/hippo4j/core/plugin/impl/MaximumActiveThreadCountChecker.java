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

package cn.hippo4j.core.plugin.impl;

import cn.hippo4j.core.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.plugin.PluginRuntime;
import cn.hippo4j.core.plugin.manager.ThreadPoolPluginSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>A plugin that checks whether the maximum number of threads in the thread pool is exceeded.
 *
 * <p>When task is submitted to the thread pool, before the current worker thread executes the task:
 * <ol>
 *     <li>check whether the maximum number of threads in the thread pool is exceeded;</li>
 *     <li>
 *         if already exceeded, interrupt the worker thread,
 *         and throw an {@link IllegalMaximumActiveCountException} exception to destroy the worker thread;
 *     </li>
 *     <li>if {@link #enableSubmitTaskAfterCheckFail} is true, re submit the task to the thread pool after check fail;</li>
 * </ol>
 *
 * <p><b>NOTE</b>: if custom {@link Thread.UncaughtExceptionHandler} is set for the thread pool,
 * it may catch the {@link IllegalMaximumActiveCountException} exception and cause the worker thread to not be destroyed.
 *
 * @author huangchengxing
 * @see IllegalMaximumActiveCountException
 */
@Slf4j
public class MaximumActiveThreadCountChecker implements ExecuteAwarePlugin {

    /**
     * Thread pool.
     */
    public final ThreadPoolPluginSupport threadPoolPluginSupport;

    /**
     * Whether to re-deliver the task to the thread pool after the maximum number of threads is exceeded.
     */
    private final AtomicBoolean enableSubmitTaskAfterCheckFail;

    /**
     * Create {@link MaximumActiveThreadCountChecker}.
     *
     * @param threadPoolPluginSupport thread pool
     * @param enableSubmitTaskAfterCheckFail whether to re-deliver the task to the thread pool after the maximum number of threads is exceeded.
     */
    public MaximumActiveThreadCountChecker(ThreadPoolPluginSupport threadPoolPluginSupport, boolean enableSubmitTaskAfterCheckFail) {
        this.threadPoolPluginSupport = threadPoolPluginSupport;
        this.enableSubmitTaskAfterCheckFail = new AtomicBoolean(enableSubmitTaskAfterCheckFail);
    }

    /**
     * Set whether to re-deliver the task to the thread pool after the maximum number of threads is exceeded.
     *
     * @param enableSubmitTaskAfterCheckFail whether to re-deliver the task to the thread pool after the maximum number of threads is exceeded.
     */
    public void setEnableSubmitTaskAfterCheckFail(boolean enableSubmitTaskAfterCheckFail) {
        this.enableSubmitTaskAfterCheckFail.set(enableSubmitTaskAfterCheckFail);
    }

    /**
     * Get whether to re-deliver the task to the thread pool after the maximum number of threads is exceeded.
     *
     * @return whether to re-deliver the task to the thread pool after the maximum number of threads is exceeded.
     */
    public boolean isEnableSubmitTaskAfterCheckFail() {
        return enableSubmitTaskAfterCheckFail.get();
    }

    /**
     * Get plugin runtime info.
     *
     * @return plugin runtime info
     */
    @Override
    public PluginRuntime getPluginRuntime() {
        return new PluginRuntime(getId())
                .addInfo("enableSubmitTaskAfterCheckFail", enableSubmitTaskAfterCheckFail.get());
    }

    /**
     * <p>Check the maximum number of threads in the thread pool before the task is executed,
     * if the maximum number of threads is exceeded,
     * an {@link IllegalMaximumActiveCountException} will be thrown.
     *
     * @param thread   thread of executing task
     * @param runnable task
     * @throws IllegalMaximumActiveCountException thread if the maximum number of threads is exceeded
     * @see ThreadPoolExecutor#beforeExecute
     */
    @Override
    public void beforeExecute(Thread thread, Runnable runnable) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolPluginSupport.getThreadPoolExecutor();
        int activeCount = threadPoolExecutor.getActiveCount();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        if (activeCount > maximumPoolSize) {
            // redelivery task if necessary
            if (enableSubmitTaskAfterCheckFail.get()) {
                log.warn(
                        "The maximum number of threads in the thread pool '{}' has been exceeded(activeCount={}, maximumPoolSize={}), task '{}' will redelivery",
                        threadPoolPluginSupport.getThreadPoolId(), activeCount, maximumPoolSize, runnable);
                submitTaskAfterCheckFail(runnable);
            } else {
                log.warn(
                        "The maximum number of threads in the thread pool '{}' has been exceeded(activeCount={}, maximumPoolSize={}), task '{}' will be discarded.",
                        threadPoolPluginSupport.getThreadPoolId(), activeCount, maximumPoolSize, runnable);
            }
            interruptAndThrowException(thread, activeCount, maximumPoolSize);
        }
    }

    /**
     * <p>Submit task to thread pool after check fail.
     *
     * @param runnable task
     */
    protected void submitTaskAfterCheckFail(Runnable runnable) {
        if (runnable instanceof RunnableFuture) {
            RunnableFuture<?> future = (RunnableFuture<?>) runnable;
            if (future.isDone()) {
                return;
            }
            if (future.isCancelled()) {
                return;
            }
        }
        threadPoolPluginSupport.getThreadPoolExecutor().execute(runnable);
    }

    private void interruptAndThrowException(Thread thread, int activeCount, int maximumPoolSize) {
        thread.interrupt();
        throw new IllegalMaximumActiveCountException(
                "The maximum number of threads in the thread pool '" + threadPoolPluginSupport.getThreadPoolId()
                        + "' has been exceeded(activeCount=" + activeCount + ", maximumPoolSize=" + maximumPoolSize + ").");
    }

    /**
     * A {@link RuntimeException} that indicates that the maximum number of threads in the thread pool has been exceeded.
     */
    protected static class IllegalMaximumActiveCountException extends RuntimeException {

        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public IllegalMaximumActiveCountException(String message) {
            super(message);
        }
    }
}
