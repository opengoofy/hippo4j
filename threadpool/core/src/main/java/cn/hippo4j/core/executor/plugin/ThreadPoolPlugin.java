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

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginRegistrar;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginSupport;

/**
 * <p>A marker superinterface indicating that
 * an instance class is eligible to be sense and intercept
 * some operations of the specific thread-pool instance.
 *
 * <p>Generally, any thread-pool that implements the {@link ThreadPoolPluginSupport}
 * can be register multiple plugins by {@link ThreadPoolPluginSupport#register},
 * and the plugin will provide some extension function of original
 * {@link java.util.concurrent.ThreadPoolExecutor} does not support.
 *
 * @see ExtensibleThreadPoolExecutor
 * @see ThreadPoolPluginRegistrar
 * @see ThreadPoolPluginManager
 * @see TaskAwarePlugin
 * @see ExecuteAwarePlugin
 * @see ShutdownAwarePlugin
 * @see RejectedAwarePlugin
 */
public interface ThreadPoolPlugin {

    /**
     * Get id, {@link Class#getSimpleName()} will be returned by default.
     *
     * @return id
     */
    default String getId() {
        return this.getClass().getSimpleName();
    }

    /**
     * Callback when plugin register into manager
     *
     * @see ThreadPoolPluginManager#register
     */
    default void start() {
    }

    /**
     * Callback when plugin unregister from manager
     *
     * @see ThreadPoolPluginManager#unregister
     * @see ThreadPoolPluginManager#clear
     */
    default void stop() {
    }

    /**
     * Get plugin runtime info.
     *
     * @return plugin runtime info
     */
    default PluginRuntime getPluginRuntime() {
        return new PluginRuntime(getId());
    }
}
