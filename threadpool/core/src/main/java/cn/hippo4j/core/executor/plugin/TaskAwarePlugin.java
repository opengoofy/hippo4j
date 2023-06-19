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

package cn.hippo4j.core.executor.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Callback during task submit in thread-pool.
 */
public interface TaskAwarePlugin extends ThreadPoolPlugin {

    /**
     * Callback during the {@link java.util.concurrent.RunnableFuture} task create in thread-pool.
     *
     * @param executor executor
     * @param runnable original task
     * @param value    the default value for the returned future
     * @param <V>      value type
     * @return Tasks that really need to be performed, if the return task is null,
     * terminate the execution of the next plugin immediately.
     * @see ThreadPoolExecutor#newTaskFor(Runnable, Object)
     */
    @Nullable
    default <V> Runnable beforeTaskCreate(ThreadPoolExecutor executor, Runnable runnable, V value) {
        return runnable;
    }

    /**
     * Callback during the {@link java.util.concurrent.RunnableFuture} task create in thread-pool.
     *
     * @param executor executor
     * @param future   original task
     * @param <V>      value type
     * @return Tasks that really need to be performed, if the return task is null,
     * terminate the execution of the next plugin immediately.
     * @see ThreadPoolExecutor#newTaskFor(Callable)
     */
    @Nullable
    default <V> Callable<V> beforeTaskCreate(ThreadPoolExecutor executor, Callable<V> future) {
        return future;
    }

    /**
     * Callback when task is execute.
     *
     * @param runnable runnable
     * @return task to be executed, if the return task is null,
     * terminate the execution of the next plug-in immediately.
     * @see ThreadPoolExecutor#execute
     */
    @Nullable
    default Runnable beforeTaskExecute(@NonNull Runnable runnable) {
        return runnable;
    }
}
