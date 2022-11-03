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

package cn.hippo4j.core.plugin.manager;

import cn.hippo4j.core.plugin.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Used to support the binding of {@link ThreadPoolPluginManager} and {@link ThreadPoolExecutor}.
 */
public interface ThreadPoolPluginSupport extends ThreadPoolPluginManager {

    /**
     * Get thread pool action aware registry.
     *
     * @return {@link ThreadPoolPluginManager}
     */
    @NonNull
    ThreadPoolPluginManager getThreadPoolPluginManager();

    /**
     * Get thread-pool id
     *
     * @return thread-pool id
     */
    String getThreadPoolId();

    /**
     * Get thread-pool executor.
     *
     * @return thread-pool executor
     */
    ThreadPoolExecutor getThreadPoolExecutor();

    // ======================== delegate methods ========================

    /**
     * Clear all.
     */
    @Override
    default void clear() {
        getThreadPoolPluginManager().clear();
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin aware
     */
    @Override
    default void register(ThreadPoolPlugin plugin) {
        getThreadPoolPluginManager().register(plugin);
    }

    /**
     * Register plugin if it's not registered.
     *
     * @param plugin plugin
     * @return return true if successful register new plugin, false otherwise
     */
    @Override
    default boolean tryRegister(ThreadPoolPlugin plugin) {
        return getThreadPoolPluginManager().tryRegister(plugin);
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId name
     * @return ture if target has been registered, false otherwise
     */
    @Override
    default boolean isRegistered(String pluginId) {
        return getThreadPoolPluginManager().isRegistered(pluginId);
    }

    /**
     * Unregister {@link ThreadPoolPlugin}.
     *
     * @param pluginId name
     */
    @Override
    default void unregister(String pluginId) {
        getThreadPoolPluginManager().unregister(pluginId);
    }

    /**
     * Get all registered plugins.
     *
     * @return plugins
     */
    @Override
    default Collection<ThreadPoolPlugin> getAllPlugins() {
        return getThreadPoolPluginManager().getAllPlugins();
    }

    /**
     * Get {@link ThreadPoolPlugin}.
     *
     * @param pluginId target name
     * @return {@link ThreadPoolPlugin}, null if unregister
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    @Override
    default <A extends ThreadPoolPlugin> Optional<A> getPlugin(String pluginId) {
        return getThreadPoolPluginManager().getPlugin(pluginId);
    }

    /**
     * Get execute aware list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    default Collection<ExecuteAwarePlugin> getExecuteAwarePluginList() {
        return getThreadPoolPluginManager().getExecuteAwarePluginList();
    }

    /**
     * Get rejected aware list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    @Override
    default Collection<RejectedAwarePlugin> getRejectedAwarePluginList() {
        return getThreadPoolPluginManager().getRejectedAwarePluginList();
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    default Collection<ShutdownAwarePlugin> getShutdownAwarePluginList() {
        return getThreadPoolPluginManager().getShutdownAwarePluginList();
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    default Collection<TaskAwarePlugin> getTaskAwarePluginList() {
        return getThreadPoolPluginManager().getTaskAwarePluginList();
    }
}
