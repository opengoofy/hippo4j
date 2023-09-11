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

import cn.hippo4j.core.executor.provider.CommonDynamicThreadPoolProviderFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dynamic thread-pool wrapper.
 *
 * @deprecated 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class DynamicThreadPoolWrapper implements DisposableBean {

    /**
     * Determine the unique identifier of the thread pool in the project.
     *
     * @param tenantId tenant id
     * @param itemId project id in the team
     * @param threadPoolId Thread pool identifier under the project
     */
    private String tenantId, itemId, threadPoolId;

    /**
     * Whether the thread pool has completed initialization,
     * and whether to subscribe to server-side configuration change events.
     *
     * @param subscribeFlag subscription server configuration id
     * @param initFlag initial configuration complete flag
     */
    private boolean subscribeFlag, initFlag;

    /**
     * Thread pool executor.
     */
    private ThreadPoolExecutor executor;

    public DynamicThreadPoolWrapper(String threadPoolId) {
        this(threadPoolId, CommonDynamicThreadPoolProviderFactory.getInstance(threadPoolId));
    }

    public DynamicThreadPoolWrapper(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolId = threadPoolId;
        this.executor = threadPoolExecutor;
        this.subscribeFlag = true;
    }

    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     * <p>
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@code RejectedExecutionHandler}.
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *                                    {@code RejectedExecutionHandler}, if the task
     *                                    cannot be accepted for execution
     * @throws NullPointerException       if {@code command} is null
     */
    public void execute(Runnable command) {
        executor.execute(command);
    }

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     *
     * <p>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * {@code result = exec.submit(aCallable).get();}
     *
     * <p>Note: The {@link Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link Callable} form so they can be submitted.
     *
     * @param task the task to submit
     * @param <T>  the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    @Override
    public void destroy() throws Exception {
        if (executor instanceof DynamicThreadPoolExecutor) {
            ((DynamicThreadPoolExecutor) executor).destroy();
        }
    }
}
