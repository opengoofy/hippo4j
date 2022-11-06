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

import cn.hippo4j.core.plugin.impl.ThreadPoolExecutorShutdownPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.*;

/**
 * Dynamic executor configuration support.
 *
 * @deprecated use {@link ThreadPoolExecutorShutdownPlugin} to get thread-pool shutdown support
 */
@Deprecated
@Slf4j
public abstract class AbstractDynamicExecutorSupport extends ThreadPoolExecutor implements InitializingBean, DisposableBean {

    private String threadPoolId;

    private ExecutorService executor;

    public long awaitTerminationMillis;

    public boolean waitForTasksToCompleteOnShutdown;

    public AbstractDynamicExecutorSupport(int corePoolSize,
                                          int maximumPoolSize,
                                          long keepAliveTime,
                                          TimeUnit unit,
                                          boolean waitForTasksToCompleteOnShutdown,
                                          long awaitTerminationMillis,
                                          BlockingQueue<Runnable> workQueue,
                                          String threadPoolId,
                                          ThreadFactory threadFactory,
                                          RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadPoolId = threadPoolId;
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        this.awaitTerminationMillis = awaitTerminationMillis;
    }

    /**
     * Create the target {@link java.util.concurrent.ExecutorService} instance.
     * Called by {@code afterPropertiesSet}.
     *
     * @return a new ExecutorService instance
     * @see #afterPropertiesSet()
     */
    protected abstract ExecutorService initializeExecutor();

    /**
     * Calls {@code initialize()} after the container applied all property values.
     *
     * @see #initialize()
     */
    @Override
    public void afterPropertiesSet() {
        initialize();
    }

    /**
     * Calls {@code shutdown} when the BeanFactory destroys.
     * the task executor instance.
     *
     * @see #shutdown()
     */
    @Override
    public void destroy() {
        shutdownSupport();
    }

    /**
     * Set up the ExecutorService.
     */
    public void initialize() {
        if (log.isInfoEnabled()) {
            log.info("Initializing ExecutorService" + (this.threadPoolId != null ? " '" + this.threadPoolId + "'" : ""));
        }

        this.executor = initializeExecutor();
    }

    /**
     * Set support param.
     *
     * @param awaitTerminationMillis
     * @param waitForTasksToCompleteOnShutdown
     */
    public void setSupportParam(long awaitTerminationMillis, boolean waitForTasksToCompleteOnShutdown) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    /**
     * Perform a shutdown on the underlying ExecutorService.
     *
     * @see java.util.concurrent.ExecutorService#shutdown()
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    public void shutdownSupport() {
        if (log.isInfoEnabled()) {
            log.info("Shutting down ExecutorService" + (this.threadPoolId != null ? " '" + this.threadPoolId + "'" : ""));
        }
        if (this.executor != null) {
            if (this.waitForTasksToCompleteOnShutdown) {
                this.executor.shutdown();
            } else {
                for (Runnable remainingTask : this.executor.shutdownNow()) {
                    cancelRemainingTask(remainingTask);
                }
            }
            awaitTerminationIfNecessary(this.executor);
        }
    }

    /**
     * Cancel the given remaining task which never commended execution,
     * as returned from {@link ExecutorService#shutdownNow()}.
     *
     * @param task the task to cancel (typically a {@link RunnableFuture})
     * @see #shutdown()
     * @see RunnableFuture#cancel(boolean)
     * @since 5.0.5
     */
    protected void cancelRemainingTask(Runnable task) {
        if (task instanceof Future) {
            ((Future<?>) task).cancel(true);
        }
    }

    /**
     * Wait for the executor to terminate, according to the value of the.
     */
    private void awaitTerminationIfNecessary(ExecutorService executor) {
        if (this.awaitTerminationMillis > 0) {
            try {
                if (!executor.awaitTermination(this.awaitTerminationMillis, TimeUnit.MILLISECONDS)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Timed out while waiting for executor" +
                                (this.threadPoolId != null ? " '" + this.threadPoolId + "'" : "") + " to terminate.");
                    }
                }
            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Interrupted while waiting for executor" +
                            (this.threadPoolId != null ? " '" + this.threadPoolId + "'" : "") + " to terminate.");
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}
