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

package cn.hippo4j.core.executor.plugin.manager;

import cn.hippo4j.core.executor.plugin.TaskAwarePlugin;
import cn.hippo4j.core.executor.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.executor.plugin.PluginRuntime;
import cn.hippo4j.core.executor.plugin.RejectedAwarePlugin;
import cn.hippo4j.core.executor.plugin.ShutdownAwarePlugin;
import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Manager of {@link ThreadPoolPlugin}. <br />
 * Bind with the specified thread-pool instance to register and manage plugins.
 * when the thread pool is destroyed, please ensure that the manager will also be destroyed.
 *
 * @see DefaultThreadPoolPluginManager
 */
public interface ThreadPoolPluginManager {

    /**
     * Get an empty manager.
     *
     * @return {@link EmptyThreadPoolPluginManager}
     */
    static ThreadPoolPluginManager empty() {
        return EmptyThreadPoolPluginManager.INSTANCE;
    }

    /**
     * Clear all.
     */
    void clear();

    /**
     * Get all registered plugins.
     *
     * @return plugins
     */
    Collection<ThreadPoolPlugin> getAllPlugins();

    /**
     * Register a {@link ThreadPoolPlugin}.
     *
     * @param plugin plugin
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()}
     *                                  already exists in the registry
     * @see ThreadPoolPlugin#getId()
     */
    void register(ThreadPoolPlugin plugin);

    /**
     * Register plugin if it's not registered.
     *
     * @param plugin plugin
     * @return return true if successful register new plugin, false otherwise
     */
    boolean tryRegister(ThreadPoolPlugin plugin);

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId plugin id
     * @return ture if target has been registered, false otherwise
     */
    boolean isRegistered(String pluginId);

    /**
     * Unregister {@link ThreadPoolPlugin}.
     *
     * @param pluginId plugin id
     */
    void unregister(String pluginId);

    /**
     * Get id of disabled plugins.
     *
     * @return id of disabled plugins
     */
    Set<String> getAllDisabledPluginIds();

    /**
     * Whether the plugin has been disabled.
     *
     * @param pluginId plugin id
     * @return true if plugin has been disabled, false otherwise
     */
    boolean isDisabled(String pluginId);

    /**
     * Enable plugin, make plugins will allow access through quick indexes.
     *
     * @param pluginId plugin id
     * @return true if plugin already registered or enabled, false otherwise
     */
    boolean enable(String pluginId);

    /**
     * Disable plugin, make plugins will not allow access through quick indexes.
     *
     * @param pluginId plugin id
     * @return true if plugin already registered or disabled, false otherwise
     */
    boolean disable(String pluginId);

    /**
     * Get registered {@link ThreadPoolPlugin}.
     *
     * @param pluginId plugin id
     * @param <A>      target aware type
     * @return {@link ThreadPoolPlugin}
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    <A extends ThreadPoolPlugin> Optional<A> getPlugin(String pluginId);

    /**
     * Get all enabled {@link ExecuteAwarePlugin}.
     *
     * @return {@link ExecuteAwarePlugin}
     * @see #enable
     * @see #disable
     */
    Collection<ExecuteAwarePlugin> getExecuteAwarePluginList();

    /**
     * Get all enabled {@link RejectedAwarePlugin}.
     *
     * @return {@link RejectedAwarePlugin}
     * @see #enable
     * @see #disable
     */
    Collection<RejectedAwarePlugin> getRejectedAwarePluginList();

    /**
     * Get all enabled {@link ShutdownAwarePlugin}.
     *
     * @return {@link ShutdownAwarePlugin}
     * @see #enable
     * @see #disable
     */
    Collection<ShutdownAwarePlugin> getShutdownAwarePluginList();

    /**
     * Get all enabled {@link TaskAwarePlugin}.
     *
     * @return {@link TaskAwarePlugin}
     * @see #enable
     * @see #disable
     */
    Collection<TaskAwarePlugin> getTaskAwarePluginList();

    // ==================== default methods ====================

    /**
     * Get plugin of type.
     *
     * @param pluginId   plugin id
     * @param pluginType plugin type
     * @return target plugin
     */
    default <A extends ThreadPoolPlugin> Optional<A> getPluginOfType(String pluginId, Class<A> pluginType) {
        return getPlugin(pluginId)
                .filter(pluginType::isInstance)
                .map(pluginType::cast);
    }

    /**
     * Get all plugins of type.
     *
     * @param pluginType plugin type
     * @return all plugins of type
     */
    default <A extends ThreadPoolPlugin> Collection<A> getAllPluginsOfType(Class<A> pluginType) {
        return getAllPlugins().stream()
                .filter(pluginType::isInstance)
                .map(pluginType::cast)
                .collect(Collectors.toList());
    }

    /**
     * Get {@link PluginRuntime} of all registered plugins.
     *
     * @return {@link PluginRuntime} of all registered plugins
     */
    default Collection<PluginRuntime> getAllPluginRuntimes() {
        return getAllPlugins().stream()
                .map(ThreadPoolPlugin::getPluginRuntime)
                .collect(Collectors.toList());
    }

    /**
     * Get {@link PluginRuntime} of registered plugin.
     *
     * @return {@link PluginRuntime} of registered plugin
     */
    default Optional<PluginRuntime> getRuntime(String pluginId) {
        return getPlugin(pluginId)
                .map(ThreadPoolPlugin::getPluginRuntime);
    }
}
