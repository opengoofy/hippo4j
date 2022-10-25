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

import cn.hippo4j.common.toolkit.SyncTimeRecorder;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.toolkit.SystemClock;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Record task execution time indicator.
 *
 * @author huangchengxing
 * @see TaskTimeoutNotifyAlarmPlugin
 */
@RequiredArgsConstructor
public class TaskTimeRecordPlugin extends SyncTimeRecorder implements ExecuteAwarePlugin {

    public static final String PLUGIN_NAME = "task-time-record-plugin";

    /**
     * Get id.
     *
     * @return id
     */
    @JsonProperty("pluginId")
    @Override
    public String getId() {
        return PLUGIN_NAME;
    }

    @JsonProperty("taskTimeInfo")
    public Summary getInfo() {
        return summarize();
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
            refreshTime(executeTime);
            afterRefreshTime(executeTime);
        } finally {
            startTimes.remove();
        }
    }

    /**
     * The callback function provided to the subclass, which is called after {@link #refreshTime}
     *
     * @param executeTime executeTime
     */
    protected void afterRefreshTime(long executeTime) {
        // do nothing
    }

}
