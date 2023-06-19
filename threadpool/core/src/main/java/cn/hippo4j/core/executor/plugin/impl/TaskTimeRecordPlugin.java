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

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.core.executor.plugin.PluginRuntime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * <p>Record task execution time indicator. <br />
 * The initialization size of the timer container can be specified during construction,
 * It will route it to different timers in the container according to the {@link Thread#getId},
 * to reduce the lock competition strength for a single timer.
 */
public class TaskTimeRecordPlugin extends AbstractTaskTimerPlugin {

    /**
     * maximumCapacity: 1 << 30
     */
    private static final int MAXIMUM_CAPACITY = 1073741824;

    /**
     * pluginName.
     */
    public static final String PLUGIN_NAME = TaskTimeRecordPlugin.class.getSimpleName();

    /**
     * modulo
     */
    private final int modulo;

    /**
     * timers
     */
    private final Timer[] timerTable;

    /**
     * The default time mills
     */
    private static final long DEFAULT_TIME_MILLS = -1L;

    /**
     * No task count
     */
    private static final long NO_TASK_COUNT = -1L;

    /**
     * All bits are values of 1
     */
    private static final int ALL_BIT_IS_ONE = -1;

    /**
     * Create a {@link TaskTimeRecordPlugin}
     *
     * @param initialCapacity initial capacity of timer table
     */
    public TaskTimeRecordPlugin(int initialCapacity) {
        Assert.isTrue(initialCapacity >= 1, "count must great then 0");
        initialCapacity = tableSizeFor(initialCapacity);
        timerTable = (Timer[]) Array.newInstance(Timer.class, initialCapacity);
        for (int i = 0; i < timerTable.length; i++) {
            timerTable[i] = new Timer();
        }
        modulo = initialCapacity - 1;
    }

    /**
     * Create a {@link TaskTimeRecordPlugin}
     */
    public TaskTimeRecordPlugin() {
        this(1);
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
                .addInfo("timerCount", timerTable.length)
                .addInfo("taskCount", summary.getTaskCount())
                .addInfo("minTaskTime", summary.getMinTaskTimeMillis() + "ms")
                .addInfo("maxTaskTime", summary.getMaxTaskTimeMillis() + "ms")
                .addInfo("totalTaskTime", summary.getTotalTaskTimeMillis() + "ms")
                .addInfo("avgTaskTime", summary.getAvgTaskTimeMillis() + "ms");
    }

    /**
     * Refresh time indicators of the current instance.
     *
     * @param taskExecuteTime execute time of task
     */
    @Override
    protected void processTaskTime(long taskExecuteTime) {
        Timer timer = getTimerForCurrentThread();
        timer.recordTaskTime(taskExecuteTime);
    }

    /**
     * Get the summary statistics of the instance at the current time.
     *
     * @return data snapshot
     */
    public Summary summarize() {
        // ignore unused timers
        List<Summary> summaries = Arrays.stream(timerTable)
                .map(Timer::summarize)
                .filter(s -> s.getTaskCount() > 0)
                .collect(Collectors.toList());

        // summarize data
        long totalTaskTimeMillis = 0L;
        long maxTaskTimeMillis = DEFAULT_TIME_MILLS;
        long minTaskTimeMillis = DEFAULT_TIME_MILLS;
        long taskCount = 0L;
        for (Summary summary : summaries) {
            if (taskCount > 0) {
                maxTaskTimeMillis = Math.max(maxTaskTimeMillis, summary.getMaxTaskTimeMillis());
                minTaskTimeMillis = Math.min(minTaskTimeMillis, summary.getMinTaskTimeMillis());
            } else {
                maxTaskTimeMillis = summary.getMaxTaskTimeMillis();
                minTaskTimeMillis = summary.getMinTaskTimeMillis();
            }
            totalTaskTimeMillis += summary.getTotalTaskTimeMillis();
            taskCount += summary.getTaskCount();
        }
        return new Summary(totalTaskTimeMillis, maxTaskTimeMillis, minTaskTimeMillis, taskCount);
    }

    private Timer getTimerForCurrentThread() {
        /*
         * use table tableSize - 1 to take modulus for tid, and the remainder obtained is the subscript of the timer corresponding to the thread in the table. eg: tid = 10086, tableSize = 8, then we
         * get 10086 & (8 - 1) = 4
         */
        long threadId = Thread.currentThread().getId();
        int index = (int) (threadId & modulo);
        return timerTable[index];
    }

    /**
     * copy from {@link HashMap#tableSizeFor}
     */
    static int tableSizeFor(int cap) {
        int n = ALL_BIT_IS_ONE >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * <p>Independent unit for providing time recording function.<br />
     * Support thread-safe operations when reading and writing in a concurrent environment.
     */
    private static class Timer {

        /**
         * Lock instance
         */
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * Total execution milli time of all tasks
         */
        private long totalTaskTimeMillis = 0L;

        /**
         * Maximum task milli execution time, default -1
         */
        private long maxTaskTimeMillis = -1L;

        /**
         * Minimal task milli execution time, default -1
         */
        private long minTaskTimeMillis = -1L;

        /**
         * Count of completed task
         */
        private long taskCount = 0L;

        /**
         * Record task execute time.
         *
         * @param taskExecuteTime task execute time
         */
        public void recordTaskTime(long taskExecuteTime) {
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                if (taskCount > 0) {
                    maxTaskTimeMillis = Math.max(taskExecuteTime, maxTaskTimeMillis);
                    minTaskTimeMillis = Math.min(taskExecuteTime, minTaskTimeMillis);
                } else {
                    maxTaskTimeMillis = taskExecuteTime;
                    minTaskTimeMillis = taskExecuteTime;
                }
                taskCount = taskCount + 1;
                totalTaskTimeMillis += taskExecuteTime;
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

    }

    /**
     * Summary statistics of SyncTimeRecorder instance at a certain time.
     */
    @Getter
    @RequiredArgsConstructor
    public static class Summary {

        /**
         * Total execution nano time of all tasks
         */
        private final long totalTaskTimeMillis;

        /**
         * Maximum task nano execution time
         */
        private final long maxTaskTimeMillis;

        /**
         * Minimal task nano execution time
         */
        private final long minTaskTimeMillis;

        /**
         * Count of completed task
         */
        private final long taskCount;

        /**
         * Get the avg task time in milliseconds
         *
         * @return avg task time
         */
        public long getAvgTaskTimeMillis() {
            long totalTaskCount = getTaskCount();
            return totalTaskCount > 0L ? getTotalTaskTimeMillis() / totalTaskCount : NO_TASK_COUNT;
        }
    }

}
