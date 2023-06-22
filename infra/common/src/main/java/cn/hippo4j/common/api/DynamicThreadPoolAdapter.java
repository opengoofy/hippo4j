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

package cn.hippo4j.common.api;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dynamic thread pool adapter.
 */
public interface DynamicThreadPoolAdapter {

    /**
     * Check if the object contains thread pool information.
     *
     * @param executor objects where there may be instances
     *                 of dynamic thread pools
     * @return matching results
     */
    boolean match(Object executor);

    /**
     * Get the dynamic thread pool reference in the object.
     *
     * @param executor objects where there may be instances
     *                 of dynamic thread pools
     * @return get the real dynamic thread pool instance
     */
    ThreadPoolExecutor unwrap(Object executor);

    /**
     * If the {@link DynamicThreadPoolAdapter#match(Object)} conditions are met,
     * the thread pool is replaced with a dynamic thread pool.
     *
     * @param executor                  objects where there may be instances
     *                                  of dynamic thread pools
     * @param dynamicThreadPoolExecutor dynamic thread-pool executor
     */
    void replace(Object executor, Executor dynamicThreadPoolExecutor);
}
