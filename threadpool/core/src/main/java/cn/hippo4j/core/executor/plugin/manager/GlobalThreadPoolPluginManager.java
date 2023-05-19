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

import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A globally {@link ThreadPoolPluginManager}.
 * It is used to manage {@link ThreadPoolPluginSupport} in the global,
 * so that all managed {@link ThreadPoolPluginSupport} can be registered incrementally.
 */
public interface GlobalThreadPoolPluginManager extends ThreadPoolPluginRegistrar {

    /**
     * Synchronize all enabled plugins and registrars
     * in the current manager to the {@link ThreadPoolPluginSupport}.
     * After that, the support will <b>not</b> be synchronized with the plug-in and registrar states in the manager.
     *
     * @param support thread pool plugin manager delegate
     * @see #registerThreadPoolPluginSupport
     */
    @Override
    void doRegister(ThreadPoolPluginSupport support);

    /**
     * Synchronize all enabled plugins and registrars
     * in the current manager to the {@link ThreadPoolPluginSupport},
     * After that, this support will be synchronized with the plug-in and registrar status in the manager.
     *
     * @param support thread pool plugin manager support
     * @return true if the support has not been managed before, false otherwise
     * @see #doRegister
     */
    boolean registerThreadPoolPluginSupport(ThreadPoolPluginSupport support);

    /**
     * Cancel the management of the specified {@link ThreadPoolPluginSupport}.
     *
     * @param threadPoolId thread pool id
     * @return {@link ThreadPoolPluginSupport} if managed, null otherwise
     */
    ThreadPoolPluginSupport cancelManagement(String threadPoolId);

    /**
     * Get registered {@link ThreadPoolPluginSupport}.
     *
     * @param threadPoolId thread-pool id
     * @return {@link ThreadPoolPluginSupport} if managed, null otherwise
     */
    ThreadPoolPluginSupport getManagedThreadPoolPluginSupport(String threadPoolId);

    /**
     * Get all registered {@link ThreadPoolPluginSupport}
     *
     * @return all registered {@link ThreadPoolPluginSupport}
     */
    Collection<ThreadPoolPluginSupport> getAllManagedThreadPoolPluginSupports();

    // ===================== plugin =====================

    /**
     * Enable plugin for all {@link ThreadPoolPluginSupport},
     * after action, newly registered support will also get this plugin.
     *
     * @param plugin plugin
     * @return true if the plugin has not been enabled before, false otherwise
     */
    boolean enableThreadPoolPlugin(ThreadPoolPlugin plugin);

    /**
     * Get all enable {@link ThreadPoolPlugin}.
     *
     * @return all published {@link ThreadPoolPlugin}
     */
    Collection<ThreadPoolPlugin> getAllEnableThreadPoolPlugins();

    /**
     * Disable {@link ThreadPoolPlugin} for all {@link ThreadPoolPluginSupport},
     * after action, newly registered support will not get this registrar.
     *
     * @param pluginId plugin id
     * @return {@link ThreadPoolPlugin} if enable, null otherwise
     */
    ThreadPoolPlugin disableThreadPoolPlugin(String pluginId);

    /**
     * Get all plugins from registered {@link ThreadPoolPluginSupport}.
     *
     * @return plugins
     */
    default Collection<ThreadPoolPlugin> getAllPluginsFromManagers() {
        return getAllManagedThreadPoolPluginSupports().stream()
                .map(ThreadPoolPluginSupport::getAllPlugins)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Get plugins of type from registered {@link ThreadPoolPluginSupport}.
     *
     * @param pluginType plugin type
     * @return plugins
     */
    default <A extends ThreadPoolPlugin> Collection<A> getPluginsOfTypeFromManagers(Class<A> pluginType) {
        return getAllPluginsFromManagers().stream()
                .filter(pluginType::isInstance)
                .map(pluginType::cast)
                .collect(Collectors.toList());
    }

    /**
     * Get plugins by id from registered {@link ThreadPoolPluginSupport}.
     *
     * @param pluginId plugin id
     * @return plugins
     */
    default Collection<ThreadPoolPlugin> getPluginsFromManagers(String pluginId) {
        return getAllManagedThreadPoolPluginSupports().stream()
                .map(manager -> manager.getPlugin(pluginId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Unregister for all registered Managers
     *
     * @param pluginId plugin id
     */
    default void unregisterForAllManagers(String pluginId) {
        getAllManagedThreadPoolPluginSupports().forEach(s -> s.unregister(pluginId));
    }

    // ===================== registrar =====================

    /**
     * Enable registrar, then apply to all registered {@link ThreadPoolPluginSupport},
     * after action, newly registered support will also get this registrar.
     *
     * @param registrar registrar
     * @return true if the registrar has not been enabled before, false otherwise
     */
    boolean enableThreadPoolPluginRegistrar(ThreadPoolPluginRegistrar registrar);

    /**
     * Get all enable {@link ThreadPoolPluginRegistrar}.
     *
     * @return all {@link ThreadPoolPluginRegistrar}.
     */
    Collection<ThreadPoolPluginRegistrar> getAllEnableThreadPoolPluginRegistrar();

    /**
     * Unable {@link ThreadPoolPluginRegistrar}, newly registered support will not get this registrar.
     *
     * @param registrarId registrar id
     * @return {@link ThreadPoolPluginRegistrar} if enable, null otherwise
     */
    ThreadPoolPluginRegistrar disableThreadPoolPluginRegistrar(String registrarId);

}
