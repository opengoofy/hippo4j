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

package cn.hippo4j.core.executor.plugin;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Callback before thread-pool shutdown.
 */
public interface ShutdownAwarePlugin extends ThreadPoolPlugin {

    /**
     * Callback before pool shutdown.
     *
     * @param executor executor
     * @see ThreadPoolExecutor#shutdown()
     * @see ThreadPoolExecutor#shutdownNow()
     */
    default void beforeShutdown(ThreadPoolExecutor executor) {
    }

    /**
     * Callback after pool shutdown.
     *
     * @param executor       executor
     * @param remainingTasks remainingTasks, or empty if no tasks left or {@link ThreadPoolExecutor#shutdown()} called
     * @see ThreadPoolExecutor#shutdown()
     * @see ThreadPoolExecutor#shutdownNow()
     */
    default void afterShutdown(ThreadPoolExecutor executor, List<Runnable> remainingTasks) {
    }

    /**
     * Callback after pool terminated.
     *
     * @param executor executor
     * @see ThreadPoolExecutor#terminated()
     */
    default void afterTerminated(ThreadPoolExecutor executor) {
    }
}
