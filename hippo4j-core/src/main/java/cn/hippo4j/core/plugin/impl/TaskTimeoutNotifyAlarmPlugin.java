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

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Send alarm notification when the execution time exceeds the threshold.
 */
@AllArgsConstructor
public class TaskTimeoutNotifyAlarmPlugin extends AbstractTaskTimerPlugin {

    public static final String PLUGIN_NAME = "task-timeout-notify-alarm-plugin";

    /**
     * threadPoolId
     */
    private final String threadPoolId;

    @Getter
    @Setter
    private Long executeTimeOut;

    /**
     * thread-pool
     */
    private final ThreadPoolExecutor threadPoolExecutor;

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
     * Check whether the task execution time exceeds {@link #executeTimeOut},
     * if it exceeds this time, send an alarm notification.
     *
     * @param taskExecuteTime execute time of task
     */
    @Override
    protected void processTaskTime(long taskExecuteTime) {
        if (taskExecuteTime <= executeTimeOut) {
            return;
        }
        Optional.ofNullable(ApplicationContextHolder.getInstance())
                .map(context -> context.getBean(ThreadPoolNotifyAlarmHandler.class))
                .ifPresent(handler -> handler.asyncSendExecuteTimeOutAlarm(
                        threadPoolId, taskExecuteTime, executeTimeOut, threadPoolExecutor));
    }

}
