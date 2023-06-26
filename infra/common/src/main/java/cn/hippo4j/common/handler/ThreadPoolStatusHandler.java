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

package cn.hippo4j.common.handler;

import cn.hippo4j.common.toolkit.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPool status handler.
 */
@Slf4j
public class ThreadPoolStatusHandler {

    private static final String RUNNING = "Running";

    private static final String TERMINATED = "Terminated";

    private static final String SHUTTING_DOWN = "Shutting down";

    private static final AtomicBoolean EXCEPTION_FLAG = new AtomicBoolean(Boolean.TRUE);

    /**
     * Get thread-pool state.
     *
     * @param executor executor
     * @return thread-pool state
     */
    public static String getThreadPoolState(ThreadPoolExecutor executor) {
        if (EXCEPTION_FLAG.get()) {
            try {
                Method runStateLessThan = ReflectUtil.getMethodByName(ThreadPoolExecutor.class, "runStateLessThan");
                ReflectUtil.setAccessible(runStateLessThan);
                AtomicInteger ctl = (AtomicInteger) ReflectUtil.getFieldValue(executor, "ctl");
                int shutdown = (int) ReflectUtil.getFieldValue(executor, "SHUTDOWN");
                boolean runStateLessThanBool = ReflectUtil.invoke(executor, runStateLessThan, ctl.get(), shutdown);
                if (runStateLessThanBool) {
                    return RUNNING;
                }
                Method runStateAtLeast = ReflectUtil.getMethodByName(ThreadPoolExecutor.class, "runStateAtLeast");
                cn.hippo4j.common.toolkit.ReflectUtil.setAccessible(runStateAtLeast);
                int terminated = (int) ReflectUtil.getFieldValue(executor, "TERMINATED");
                String resultStatus = ReflectUtil.invoke(executor, runStateAtLeast, ctl.get(), terminated) ? TERMINATED : SHUTTING_DOWN;
                return resultStatus;
            } catch (Exception ex) {
                log.error("Failed to get thread pool status.", ex);
                EXCEPTION_FLAG.set(Boolean.FALSE);
            }
        }
        return "UNKNOWN";
    }
}
