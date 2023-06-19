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

package cn.hippo4j.threadpool.alarm.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread-pol check alarm.
 *
 * <p>Dynamic thread pool check and send logic wait for refactoring,
 * Try not to rely on this component for custom extensions, because it is undefined.
 */
public interface ThreadPoolCheckAlarm {

    /**
     * Get a none thread pool check alarm.
     *
     * @return {@link ThreadPoolCheckAlarm}
     * @see NoneThreadPoolCheckAlarm
     */
    static ThreadPoolCheckAlarm none() {
        return NoneThreadPoolCheckAlarm.INSTANCE;
    }

    /**
     * Execute scheduled tasks to scan the running status of the thread pool.
     */
    default void scheduleExecute() {
    }

    /**
     * Check pool capacity alarm.
     *
     * @param threadPoolId       thread-pool id
     * @param threadPoolExecutor thread-pool executor
     */
    void checkPoolCapacityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor);

    /**
     * Check pool activity alarm.
     *
     * @param threadPoolId       thread-pool id
     * @param threadPoolExecutor thread-pool executor
     */
    void checkPoolActivityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor);

    /**
     * Async send rejected alarm.
     *
     * @param threadPoolId thread-pool id
     */
    void asyncSendRejectedAlarm(String threadPoolId);

    /**
     * Async send execute time-out alarm.
     *
     * @param threadPoolId       thread-pool id
     * @param executeTime        execute time
     * @param executeTimeOut     execute time-out
     * @param threadPoolExecutor thread-pool executor
     */
    void asyncSendExecuteTimeOutAlarm(String threadPoolId, long executeTime, long executeTimeOut, ThreadPoolExecutor threadPoolExecutor);

    /**
     * None implementation of {@link ThreadPoolCheckAlarm}.
     *
     * @see #none()
     */
    @Slf4j
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class NoneThreadPoolCheckAlarm implements ThreadPoolCheckAlarm {

        /**
         * Default singleton.
         */
        private static final NoneThreadPoolCheckAlarm INSTANCE = new NoneThreadPoolCheckAlarm();

        /**
         * Check pool capacity alarm.
         *
         * @param threadPoolId       thread-pool id
         * @param threadPoolExecutor thread-pool executor
         */
        @Override
        public void checkPoolCapacityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
            log.info("Ignore check pool capacity alarm for ExecuteService '{}'", threadPoolId);
        }

        /**
         * Check pool activity alarm.
         *
         * @param threadPoolId       thread-pool id
         * @param threadPoolExecutor thread-pool executor
         */
        @Override
        public void checkPoolActivityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
            log.info("Ignore check pool activity alarm for ExecuteService '{}'", threadPoolId);
        }

        /**
         * Async send rejected alarm.
         *
         * @param threadPoolId thread-pool id
         */
        @Override
        public void asyncSendRejectedAlarm(String threadPoolId) {
            log.debug("Ignore async send rejected alarm for ExecuteService '{}'", threadPoolId);
        }

        /**
         * Async send execute time-out alarm.
         *
         * @param threadPoolId       thread-pool id
         * @param executeTime        execute time
         * @param executeTimeOut     execute time-out
         * @param threadPoolExecutor thread-pool executor
         */
        @Override
        public void asyncSendExecuteTimeOutAlarm(String threadPoolId, long executeTime, long executeTimeOut, ThreadPoolExecutor threadPoolExecutor) {
            log.debug("Ignore async send execute time out alarm for ExecuteService '{}'", threadPoolId);
        }
    }
}
