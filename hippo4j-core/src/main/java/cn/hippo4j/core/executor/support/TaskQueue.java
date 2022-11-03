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

import lombok.Setter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Task queue.
 */
public class TaskQueue<R extends Runnable> extends LinkedBlockingQueue<Runnable> {

    private static final long serialVersionUID = -2635853580887179627L;

    @Setter
    private FastThreadPoolExecutor executor;

    public TaskQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(Runnable runnable) {
        int currentPoolThreadSize = executor.getPoolSize();
        // If a core thread is idle, add the task to the blocking queue, and the core thread will process the task.
        if (executor.getSubmittedTaskCount() < currentPoolThreadSize) {
            return super.offer(runnable);
        }
        // The current number of threads in the thread pool is less than the maximum number of threads, and returns false.
        // According to the thread pool source code, non-core threads will be created.
        if (currentPoolThreadSize < executor.getMaximumPoolSize()) {
            return false;
        }
        // If the current thread pool number is greater than the maximum number of threads, the task is added to the blocking queue.
        return super.offer(runnable);
    }

    /**
     * Retry offer.
     *
     * @param runnable submit thread pool task
     * @param timeout  how long to wait before giving up, in units of
     *                 {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the
     *                 {@code timeout} parameter
     * @return
     * @throws InterruptedException
     */
    public boolean retryOffer(Runnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Actuator closed!");
        }
        return super.offer(runnable, timeout, unit);
    }
}
