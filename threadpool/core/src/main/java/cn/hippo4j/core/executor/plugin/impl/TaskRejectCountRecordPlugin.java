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

import cn.hippo4j.core.executor.plugin.PluginRuntime;
import cn.hippo4j.core.executor.plugin.RejectedAwarePlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Record the number of tasks rejected by the thread pool.
 */
public class TaskRejectCountRecordPlugin implements RejectedAwarePlugin {

    public static final String PLUGIN_NAME = TaskRejectCountRecordPlugin.class.getSimpleName();

    /**
     * Rejection count
     */
    @Setter
    @Getter
    private AtomicLong rejectCount = new AtomicLong(0);

    /**
     * Get plugin runtime info.
     *
     * @return plugin runtime info
     */
    @Override
    public PluginRuntime getPluginRuntime() {
        return new PluginRuntime(getId())
                .addInfo("rejectCount", getRejectCountNum());
    }

    /**
     * Record rejection count.
     *
     * @param r        task
     * @param executor executor
     */
    @Override
    public void beforeRejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        rejectCount.incrementAndGet();
    }

    /**
     * Get reject count num.
     *
     * @return reject count num
     */
    public Long getRejectCountNum() {
        return rejectCount.get();
    }
}
