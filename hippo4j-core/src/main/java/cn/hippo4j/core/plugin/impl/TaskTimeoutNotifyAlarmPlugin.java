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
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * Record task execution time indicator,
 * and send alarm notification when the execution time exceeds the threshold.
 *
 * @author huangchengxing
 */
@AllArgsConstructor
public class TaskTimeoutNotifyAlarmPlugin extends TaskTimeRecordAwareProcessorPlugin {

    public static final String PLUGIN_NAME = "task-timeout-notify-alarm-plugin";

    /**
     * Get id.
     *
     * @return id
     */
    @Override
    public String getId() {
        return PLUGIN_NAME;
    }

    @Getter
    @Setter
    private Long executeTimeOut;

    /**
     * thread-pool
     */
    private final ExtensibleThreadPoolExecutor threadPoolExecutor;

    /**
     * Check whether the task execution time exceeds {@link #executeTimeOut},
     * if it exceeds this time, send an alarm notification.
     *
     * @param executeTime executeTime in nanosecond
     */
    @Override
    public void afterRefreshTime(long executeTime) {
        if (executeTime <= executeTimeOut) {
            return;
        }
        Optional.ofNullable(ApplicationContextHolder.getInstance())
                .map(context -> context.getBean(ThreadPoolNotifyAlarmHandler.class))
                .ifPresent(handler -> handler.asyncSendExecuteTimeOutAlarm(
                        threadPoolExecutor.getThreadPoolId(), executeTime, executeTimeOut, threadPoolExecutor));
    }
}
