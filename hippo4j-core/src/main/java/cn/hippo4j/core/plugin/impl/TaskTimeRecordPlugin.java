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

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.plugin.PluginRuntime;
import cn.hippo4j.core.toolkit.SystemClock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Record task execution time indicator.
 *
 * @see TaskTimeoutNotifyAlarmPlugin
 */
@RequiredArgsConstructor
public class TaskTimeRecordPlugin implements ExecuteAwarePlugin {

    public static final String PLUGIN_NAME = "task-time-record-plugin";

    /**
     * Lock instance.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Total execution milli time of all tasks.
     */
    private long totalTaskTimeMillis = 0L;

    /**
     * Maximum task milli execution time, default -1.
     */
    private long maxTaskTimeMillis = -1L;

    /**
     * Minimal task milli execution time, default -1.
     */
    private long minTaskTimeMillis = -1L;

    /**
     * Count of completed task.
     */
    private long taskCount = 0L;

    /**
     * Get id.
     *
     * @return id
     */
    @Override
    public String getId() {
        return PLUGIN_NAME;
    }

    /**
     * start times of executed tasks
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
    public void beforeExecute(Thread thread, Runnable runnable) {
        startTimes.set(SystemClock.now());
    }

    /**
     * Get plugin runtime info.
     *
     * @return plugin runtime info
     */
    @Override
    public PluginRuntime getPluginRuntime() {
        Summary summary = summarize();
        return new PluginRuntime(getId())
                .addInfo("taskCount", summary.getTaskCount())
                .addInfo("minTaskTime", summary.getMinTaskTimeMillis() + "ms")
                .addInfo("maxTaskTime", summary.getMaxTaskTimeMillis() + "ms")
                .addInfo("totalTaskTime", summary.getTotalTaskTimeMillis() + "ms")
                .addInfo("avgTaskTime", summary.getAvgTaskTimeMillis() + "ms");
    }

    /**
     * Record the total time for the worker thread to complete the task, and update the time record.
     *
     * @param runnable runnable
     * @param throwable exception thrown during execution
     */
    @Override
    public void afterExecute(Runnable runnable, Throwable throwable) {
        try {
            Long startTime = startTimes.get();
            if (Objects.isNull(startTime)) {
                return;
            }
            long executeTime = SystemClock.now() - startTime;
            recordTaskTime(executeTime);
        } finally {
            startTimes.remove();
        }
    }

    /**
     * Refresh time indicators of the current instance.
     *
     * @param taskExecutionTime millisecond
     */
    protected void recordTaskTime(long taskExecutionTime) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (taskCount == 0) {
                maxTaskTimeMillis = taskExecutionTime;
                minTaskTimeMillis = taskExecutionTime;
            } else {
                maxTaskTimeMillis = Math.max(taskExecutionTime, maxTaskTimeMillis);
                minTaskTimeMillis = Math.min(taskExecutionTime, minTaskTimeMillis);
            }
            taskCount = taskCount + 1;
            totalTaskTimeMillis += taskExecutionTime;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get the summary statistics of the instance at the current time.
     *
     * @return data snapshot
     */
    public Summary summarize() {
        Lock readLock = lock.readLock();
        Summary statistics;
        readLock.lock();
        try {
            statistics = new Summary(
                    this.totalTaskTimeMillis,
                    this.maxTaskTimeMillis,
                    this.minTaskTimeMillis,
                    this.taskCount);
        } finally {
            readLock.unlock();
        }
        return statistics;
    }

    /**
     * Summary statistics of SyncTimeRecorder instance at a certain time.
     */
    @Getter
    @RequiredArgsConstructor
    public static class Summary {

        /**
         * Total execution nano time of all tasks.
         */
        private final long totalTaskTimeMillis;

        /**
         * Maximum task nano execution time.
         */
        private final long maxTaskTimeMillis;

        /**
         * Minimal task nano execution time.
         */
        private final long minTaskTimeMillis;

        /**
         * Count of completed task.
         */
        private final long taskCount;

        /**
         * Get the avg task time in milliseconds.
         *
         * @return avg task time
         */
        public long getAvgTaskTimeMillis() {
            long totalTaskCount = getTaskCount();
            return totalTaskCount > 0L ? getTotalTaskTimeMillis() / totalTaskCount : -1;
        }

    }

}
