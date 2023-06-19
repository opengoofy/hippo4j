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

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.toolkit.SystemClock;

import java.util.Optional;

/**
 * <p>An abstract task execution time recording plugin
 * for thread-safe statistics the execution time of tasks.
 *
 * <p>Must override {@link #processTaskTime} to define the processing logic for task execution time. <br />
 * Default time precision is milliseconds, may override {@link #currentTime} to redefine the time precision.
 *
 * @see TaskTimeRecordPlugin
 * @see TaskTimeoutNotifyAlarmPlugin
 */
public abstract class AbstractTaskTimerPlugin implements ExecuteAwarePlugin {

    /**
     * Start times of executed tasks
     */
    private final ThreadLocal<Long> startTimes = new ThreadLocal<>();

    /**
     * Record the time when the worker thread starts executing the task.
     *
     * @param thread   thread of executing task
     * @param runnable task
     * @see ExtensibleThreadPoolExecutor#beforeExecute
     */
    @Override
    public final void beforeExecute(Thread thread, Runnable runnable) {
        startTimes.set(currentTime());
    }

    /**
     * Record the total time for the worker thread to complete the task, and update the time record.
     *
     * @param runnable  runnable
     * @param throwable exception thrown during execution
     */
    @Override
    public final void afterExecute(Runnable runnable, Throwable throwable) {
        try {
            Optional.ofNullable(startTimes.get())
                    .map(startTime -> currentTime() - startTime)
                    .ifPresent(this::processTaskTime);
        } finally {
            startTimes.remove();
        }
    }

    /**
     * Get the current time.
     *
     * @return current time
     */
    protected long currentTime() {
        return SystemClock.now();
    }

    /**
     * Processing the execution time of the task.
     *
     * @param taskExecuteTime execute time of task
     */
    protected abstract void processTaskTime(long taskExecuteTime);
}
