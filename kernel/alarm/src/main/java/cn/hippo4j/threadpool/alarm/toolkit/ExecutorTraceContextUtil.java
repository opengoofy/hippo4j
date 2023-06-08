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

package cn.hippo4j.threadpool.alarm.toolkit;

import org.slf4j.MDC;

import static cn.hippo4j.common.constant.Constants.EXECUTE_TIMEOUT_TRACE;

/**
 * Trace context util.
 */
public class ExecutorTraceContextUtil {

    /**
     * Execute timeout trace key.
     */
    private static String executeTimeoutTraceKey = EXECUTE_TIMEOUT_TRACE;

    /**
     * Get and remove.
     *
     * @return timeout trace
     */
    public static String getAndRemoveTimeoutTrace() {
        String val = MDC.get(executeTimeoutTraceKey);
        MDC.remove(executeTimeoutTraceKey);
        return val;
    }

    /**
     * Put timeout trace.
     *
     * @param trace trace
     */
    public static void putTimeoutTrace(String trace) {
        MDC.put(EXECUTE_TIMEOUT_TRACE, trace);
    }

    /**
     * Set timeout trace key.
     *
     * @param key trace key
     */
    public static void setTimeoutTraceKey(String key) {
        executeTimeoutTraceKey = key;
    }
}
