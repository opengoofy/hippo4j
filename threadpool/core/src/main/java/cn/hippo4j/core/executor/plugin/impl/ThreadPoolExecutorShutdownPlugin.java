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

package cn.hippo4j.core.executor.plugin.impl;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.PluginRuntime;
import cn.hippo4j.core.executor.plugin.ShutdownAwarePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>After the thread pool calls {@link ThreadPoolExecutor#shutdown()} or {@link ThreadPoolExecutor#shutdownNow()}. <br />
 * Cancel the remaining tasks in the pool, then wait for the thread pool to terminate until
 * the blocked main thread has timed out or the thread pool has completely terminated.
 */
@Accessors(chain = true)
@Getter
@Slf4j
@AllArgsConstructor
public class ThreadPoolExecutorShutdownPlugin implements ShutdownAwarePlugin {

    public static final String PLUGIN_NAME = ThreadPoolExecutorShutdownPlugin.class.getSimpleName();

    /**
     * Await termination millis
     */
    @Setter
    private long awaitTerminationMillis;

    /**
     * Callback before pool shutdown.
     *
     * @param executor executor
     */
    @Override
    public void beforeShutdown(ThreadPoolExecutor executor) {
        if (executor instanceof ExtensibleThreadPoolExecutor) {
            ExtensibleThreadPoolExecutor dynamicThreadPoolExecutor = (ExtensibleThreadPoolExecutor) executor;
            String threadPoolId = dynamicThreadPoolExecutor.getThreadPoolId();
            if (log.isInfoEnabled()) {
                log.info("Before shutting down ExecutorService {}", threadPoolId);
            }
        }
    }

    /**
     * Callback after pool shutdown. <br />
     * cancel the remaining tasks,
     * then wait for pool to terminate according {@link #awaitTerminationMillis} if necessary.
     *
     * @param executor       executor
     * @param remainingTasks remainingTasks
     */
    @Override
    public void afterShutdown(ThreadPoolExecutor executor, List<Runnable> remainingTasks) {
        if (executor instanceof ExtensibleThreadPoolExecutor) {
            ExtensibleThreadPoolExecutor pool = (ExtensibleThreadPoolExecutor) executor;
            if (CollectionUtil.isNotEmpty(remainingTasks)) {
                remainingTasks.forEach(this::cancelRemainingTask);
            }
            awaitTerminationIfNecessary(pool);
        }
    }

    /**
     * Get plugin runtime info.
     *
     * @return plugin runtime info
     */
    @Override
    public PluginRuntime getPluginRuntime() {
        return new PluginRuntime(getId())
                .addInfo("awaitTerminationMillis", awaitTerminationMillis + "ms");
    }

    /**
     * Cancel the given remaining task which never commended execution,
     * as returned from {@link ExecutorService#shutdownNow()}.
     *
     * @param task the task to cancel (typically a {@link RunnableFuture})
     * @see RunnableFuture#cancel(boolean)
     * @since 5.0.5
     */
    protected void cancelRemainingTask(Runnable task) {
        if (task instanceof Future) {
            ((Future<?>) task).cancel(true);
        }
    }

    /**
     * Wait for the executor to terminate, according to the value of {@link #awaitTerminationMillis}.
     */
    private void awaitTerminationIfNecessary(ExtensibleThreadPoolExecutor executor) {
        String threadPoolId = executor.getThreadPoolId();
        if (this.awaitTerminationMillis <= 0) {
            return;
        }
        try {
            boolean isTerminated = executor.awaitTermination(this.awaitTerminationMillis, TimeUnit.MILLISECONDS);
            if (!isTerminated && log.isWarnEnabled()) {
                log.warn("Timed out while waiting for executor {} to terminate.", threadPoolId);
            } else {
                log.info("ExecutorService {} has been shutdown.", threadPoolId);
            }
        } catch (InterruptedException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Interrupted while waiting for executor {} to terminate.", threadPoolId);
            }
            Thread.currentThread().interrupt();
        }
    }
}
