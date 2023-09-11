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

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.RejectedAwarePlugin;
import cn.hippo4j.threadpool.alarm.api.ThreadPoolCheckAlarm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Send alert notification when a task is rejected.
 */
@RequiredArgsConstructor
public class TaskRejectNotifyAlarmPlugin implements RejectedAwarePlugin {

    public static final String PLUGIN_NAME = TaskRejectNotifyAlarmPlugin.class.getSimpleName();

    /**
     * Thread pool check alarm
     */
    @NonNull
    private final ThreadPoolCheckAlarm threadPoolCheckAlarm;

    /**
     * Create a {@link TaskRejectNotifyAlarmPlugin}
     */
    public TaskRejectNotifyAlarmPlugin() {
        this(
                Optional.ofNullable(ApplicationContextHolder.getInstance())
                        .map(context -> context.getBean(ThreadPoolCheckAlarm.class))
                        .orElseGet(ThreadPoolCheckAlarm::none));
    }

    /**
     * Callback before task is rejected.
     *
     * @param runnable task
     * @param executor executor
     */
    @Override
    public void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (!(executor instanceof ExtensibleThreadPoolExecutor)) {
            return;
        }
        String threadPoolId = ((ExtensibleThreadPoolExecutor) executor).getThreadPoolId();
        threadPoolCheckAlarm.asyncSendRejectedAlarm(threadPoolId);
    }
}
