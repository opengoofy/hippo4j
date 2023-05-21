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
import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link GlobalThreadPoolPluginManager}.
 */
public class DefaultGlobalThreadPoolPluginManager implements GlobalThreadPoolPluginManager {

    /**
     * enable thread pool plugins
     */
    private final Map<String, ThreadPoolPlugin> enableThreadPoolPlugins = new ConcurrentHashMap<>(8);

    /**
     * enable thread pool plugin registrars
     */
    private final Map<String, ThreadPoolPluginRegistrar> enableThreadPoolPluginRegistrars = new ConcurrentHashMap<>(8);

    /**
     * registered supports
     */
    private final Map<String, ThreadPoolPluginSupport> managedThreadPoolPluginSupports = new ConcurrentHashMap<>(32);

    /**
     * Synchronize all enabled plugins and registrars in the current manager to the {@link ThreadPoolPluginSupport}
     * <b>whether the support has been managed by the current manager</b>.
     * After that, the support will <b>not</b> be synchronized with the plug-in and registrar states in the manager.
     *
     * @param support thread pool plugin manager delegate
     * @see #registerThreadPoolPluginSupport
     */
    @Override
    public void doRegister(@NonNull ThreadPoolPluginSupport support) {
        enableThreadPoolPluginRegistrars.values().forEach(registrar -> registrar.doRegister(support));
        enableThreadPoolPlugins.values().forEach(support::tryRegister);
    }

    /**
     * Synchronize all enabled plugins and registrars
     * in the current manager to the {@link ThreadPoolPluginSupport} <b>if the support has not been managed before</b>,
     * After that, this support will be synchronized with the plug-in and registrar status in the manager.
     *
     * @param support thread pool plugin manager support
     * @return true if the support has not been managed before, false otherwise
     * @see #doRegister
     */
    @Override
    public boolean registerThreadPoolPluginSupport(@NonNull ThreadPoolPluginSupport support) {
        if (!managedThreadPoolPluginSupports.containsKey(support.getThreadPoolId())) {
            enableThreadPoolPluginRegistrars.values().forEach(registrar -> registrar.doRegister(support));
            enableThreadPoolPlugins.values().forEach(support::tryRegister);
            managedThreadPoolPluginSupports.put(support.getThreadPoolId(), support);
            return true;
        }
        return false;
    }

    /**
     * Cancel the management of the specified {@link ThreadPoolPluginSupport}.
     *
     * @param threadPoolId thread pool id
     * @return {@link ThreadPoolPluginSupport}
     */
    @Override
    public ThreadPoolPluginSupport cancelManagement(String threadPoolId) {
        return managedThreadPoolPluginSupports.remove(threadPoolId);
    }

    /**
     * Get registered {@link ThreadPoolPluginSupport}.
     *
     * @param threadPoolId thread-pool id
     * @return cn.hippo4j.core.plugin.manager.ThreadPoolPluginManager
     */
    @Nullable
    @Override
    public ThreadPoolPluginSupport getManagedThreadPoolPluginSupport(String threadPoolId) {
        return managedThreadPoolPluginSupports.get(threadPoolId);
    }

    /**
     * Get all registered {@link ThreadPoolPluginSupport}
     *
     * @return all registered {@link ThreadPoolPluginSupport}
     */
    @Override
    public Collection<ThreadPoolPluginSupport> getAllManagedThreadPoolPluginSupports() {
        return managedThreadPoolPluginSupports.values();
    }

    /**
     * Enable plugin for all {@link ThreadPoolPluginSupport},
     * after action, newly registered support will also get this plugin.
     *
     * @param plugin plugin
     * @return true if the plugin has not been enabled before, false otherwise
     */
    @Override
    public boolean enableThreadPoolPlugin(@NonNull ThreadPoolPlugin plugin) {
        if (Objects.isNull(enableThreadPoolPlugins.put(plugin.getId(), plugin))) {
            managedThreadPoolPluginSupports.values().forEach(support -> support.register(plugin));
            return true;
        }
        return false;
    }

    /**
     * Get all enable {@link ThreadPoolPlugin}.
     *
     * @return all published {@link ThreadPoolPlugin}
     */
    @Override
    public Collection<ThreadPoolPlugin> getAllEnableThreadPoolPlugins() {
        return enableThreadPoolPlugins.values();
    }

    /**
     * Disable {@link ThreadPoolPlugin} for all {@link ThreadPoolPluginSupport},
     * after action, newly registered support will not get this registrar.
     *
     * @param pluginId plugin id
     * @return {@link ThreadPoolPlugin} if enable, null otherwise
     */
    @Override
    public ThreadPoolPlugin disableThreadPoolPlugin(String pluginId) {
        ThreadPoolPlugin removed = enableThreadPoolPlugins.remove(pluginId);
        if (Objects.nonNull(removed)) {
            managedThreadPoolPluginSupports.values().forEach(support -> support.unregister(pluginId));
        }
        return removed;
    }

    /**
     * Enable registrar, then apply to all registered {@link ThreadPoolPluginSupport},
     * after action, newly registered support will also get this registrar.
     *
     * @param registrar registrar
     * @return true if the registrar has not been enabled before, false otherwise
     */
    @Override
    public boolean enableThreadPoolPluginRegistrar(@NonNull ThreadPoolPluginRegistrar registrar) {
        if (Objects.isNull(enableThreadPoolPluginRegistrars.put(registrar.getId(), registrar))) {
            managedThreadPoolPluginSupports.values().forEach(registrar::doRegister);
            return true;
        }
        return false;
    }

    /**
     * Get all enable {@link ThreadPoolPluginRegistrar}.
     *
     * @return all {@link ThreadPoolPluginRegistrar}.
     */
    @Override
    public Collection<ThreadPoolPluginRegistrar> getAllEnableThreadPoolPluginRegistrar() {
        return enableThreadPoolPluginRegistrars.values();
    }

    /**
     * Unable {@link ThreadPoolPluginRegistrar}, newly registered support will not get this registrar.
     *
     * @param registrarId registrar id
     * @return {@link ThreadPoolPluginRegistrar} if enable, null otherwise
     */
    @Override
    public ThreadPoolPluginRegistrar disableThreadPoolPluginRegistrar(String registrarId) {
        return enableThreadPoolPluginRegistrars.remove(registrarId);
    }

}
