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

package cn.hippo4j.common.toolkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread safe time recorder,
 * used to record multiple time periods and count various time indicators.
 *
 * @author huangchengxing
 */
public class SyncTimeRecorder {

    /**
     * Lock instance.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Total execution milli time of all tasks.
     */
    private long totalTaskTime = 0L;

    /**
     * Maximum task milli execution time, default -1.
     */
    private long maxTaskTime = -1L;

    /**
     * Minimal task milli execution time, default -1.
     */
    private long minTaskTime = -1L;

    /**
     * Count of completed task.
     */
    private long taskCount = 0L;

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
                    this.totalTaskTime,
                    this.maxTaskTime,
                    this.minTaskTime,
                    this.taskCount);
        } finally {
            readLock.unlock();
        }
        return statistics;
    }

    /**
     * Refresh time indicators of the current instance.
     *
     * @param taskExecutionTime millisecond
     */
    public final void refreshTime(long taskExecutionTime) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (taskCount == 0) {
                maxTaskTime = taskExecutionTime;
                minTaskTime = taskExecutionTime;
            } else {
                maxTaskTime = Math.max(taskExecutionTime, maxTaskTime);
                minTaskTime = Math.min(taskExecutionTime, minTaskTime);
            }
            taskCount = taskCount + 1;
            totalTaskTime += taskExecutionTime;
        } finally {
            writeLock.unlock();
        }
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
        private final long totalTaskTime;

        /**
         * Maximum task nano execution time.
         */
        private final long maxTaskTime;

        /**
         * Minimal task nano execution time.
         */
        private final long minTaskTime;

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
            return getTotalTaskTime() / getTaskCount();
        }

    }

}
