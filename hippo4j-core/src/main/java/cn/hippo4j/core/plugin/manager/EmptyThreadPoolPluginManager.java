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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Empty thread pool plugin manager.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyThreadPoolPluginManager implements ThreadPoolPluginManager {

    /**
     * Default instance
     */
    public static final EmptyThreadPoolPluginManager INSTANCE = new EmptyThreadPoolPluginManager();

    /**
     * Clear all.
     */
    @Override
    public void clear() {
    }

    /**
     * Get all registered plugins.
     *
     * @return plugins
     */
    @Override
    public Collection<ThreadPoolPlugin> getAllPlugins() {
        return Collections.emptyList();
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin plugin
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()}
     *                                  already exists in the registry
     * @see ThreadPoolPlugin#getId()
     */
    @Override
    public void register(ThreadPoolPlugin plugin) {
    }

    /**
     * Register plugin if it's not registered.
     *
     * @param plugin plugin
     * @return return true if successful register new plugin, false otherwise
     */
    @Override
    public boolean tryRegister(ThreadPoolPlugin plugin) {
        return false;
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId plugin id
     * @return ture if target has been registered, false otherwise
     */
    @Override
    public boolean isRegistered(String pluginId) {
        return false;
    }

    /**
     * Unregister {@link ThreadPoolPlugin}.
     *
     * @param pluginId plugin id
     */
    @Override
    public void unregister(String pluginId) {
    }

    /**
     * Get {@link ThreadPoolPlugin}.
     *
     * @param pluginId plugin id
     * @return {@link ThreadPoolPlugin}
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    @Override
    public <A extends ThreadPoolPlugin> Optional<A> getPlugin(String pluginId) {
        return Optional.empty();
    }

    /**
     * Get execute aware plugin list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    public Collection<ExecuteAwarePlugin> getExecuteAwarePluginList() {
        return Collections.emptyList();
    }

    /**
     * Get rejected aware plugin list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    @Override
    public Collection<RejectedAwarePlugin> getRejectedAwarePluginList() {
        return Collections.emptyList();
    }

    /**
     * Get shutdown aware plugin list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    public Collection<ShutdownAwarePlugin> getShutdownAwarePluginList() {
        return Collections.emptyList();
    }

    /**
     * Get shutdown aware plugin list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    public Collection<TaskAwarePlugin> getTaskAwarePluginList() {
        return Collections.emptyList();
    }
}
