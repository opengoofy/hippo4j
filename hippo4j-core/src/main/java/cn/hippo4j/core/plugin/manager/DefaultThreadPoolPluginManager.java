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

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.core.plugin.*;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>The default implementation of {@link ThreadPoolPluginManager}.
 * Provide basic {@link ThreadPoolPlugin} registration, logout and acquisition functions.
 * Most APIs ensure limited thread-safe.
 *
 * <p>Usually registered to {@link cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor},
 * or bound to an {@link java.util.concurrent.ThreadPoolExecutor} instance through {@link ThreadPoolPluginSupport}
 * to support its plugin based extension functions.
 *
 * <p><b>NOTE:</b>
 * When the list of plugins is obtained through the {@code getXXX} method of manager, the list is not immutable.
 * This means that until actually start iterating over the list,
 * registering or unregistering plugins through the manager will affect the results of the iteration.
 * Therefore, we should try to ensure that <b>get the latest plugin list from the manager before each use</b>.
 *
 * @see cn.hippo4j.core.executor.DynamicThreadPoolExecutor
 * @see cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor
 */
public class DefaultThreadPoolPluginManager implements ThreadPoolPluginManager {

    /**
     * Lock of this instance
     */
    private final ReadWriteLock instanceLock = new ReentrantReadWriteLock();

    /**
     * Registered {@link ThreadPoolPlugin}
     */
    private final Map<String, ThreadPoolPlugin> registeredPlugins = new ConcurrentHashMap<>(16);

    /**
     * Registered {@link TaskAwarePlugin}
     */
    private final List<TaskAwarePlugin> taskAwarePluginList = new CopyOnWriteArrayList<>();

    /**
     * Registered {@link ExecuteAwarePlugin}
     */
    private final List<ExecuteAwarePlugin> executeAwarePluginList = new CopyOnWriteArrayList<>();

    /**
     * Registered {@link RejectedAwarePlugin}
     */
    private final List<RejectedAwarePlugin> rejectedAwarePluginList = new CopyOnWriteArrayList<>();

    /**
     * Registered {@link ShutdownAwarePlugin}
     */
    private final List<ShutdownAwarePlugin> shutdownAwarePluginList = new CopyOnWriteArrayList<>();

    /**
     * Clear all.
     */
    @Override
    public synchronized void clear() {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            Collection<ThreadPoolPlugin> plugins = registeredPlugins.values();
            registeredPlugins.clear();
            taskAwarePluginList.clear();
            executeAwarePluginList.clear();
            rejectedAwarePluginList.clear();
            shutdownAwarePluginList.clear();
            plugins.forEach(ThreadPoolPlugin::stop);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin plugin
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()} already exists in the registry
     * @see ThreadPoolPlugin#getId()
     */
    @Override
    public void register(@NonNull ThreadPoolPlugin plugin) {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            String id = plugin.getId();
            Assert.isTrue(!isRegistered(id), "The plugin with id [" + id + "] has been registered");
            registeredPlugins.put(id, plugin);
            if (plugin instanceof TaskAwarePlugin) {
                taskAwarePluginList.add((TaskAwarePlugin) plugin);
            }
            if (plugin instanceof ExecuteAwarePlugin) {
                executeAwarePluginList.add((ExecuteAwarePlugin) plugin);
            }
            if (plugin instanceof RejectedAwarePlugin) {
                rejectedAwarePluginList.add((RejectedAwarePlugin) plugin);
            }
            if (plugin instanceof ShutdownAwarePlugin) {
                shutdownAwarePluginList.add((ShutdownAwarePlugin) plugin);
            }
            plugin.start();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Register plugin if it's not registered.
     *
     * @param plugin plugin
     * @return return true if successful register new plugin, false otherwise
     */
    @Override
    public boolean tryRegister(ThreadPoolPlugin plugin) {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            if (registeredPlugins.containsKey(plugin.getId())) {
                return false;
            }
            register(plugin);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param pluginId plugin id
     */
    @Override
    public void unregister(String pluginId) {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            Optional.ofNullable(pluginId)
                    .map(registeredPlugins::remove)
                    .ifPresent(plugin -> {
                        if (plugin instanceof TaskAwarePlugin) {
                            taskAwarePluginList.remove(plugin);
                        }
                        if (plugin instanceof ExecuteAwarePlugin) {
                            executeAwarePluginList.remove(plugin);
                        }
                        if (plugin instanceof RejectedAwarePlugin) {
                            rejectedAwarePluginList.remove(plugin);
                        }
                        if (plugin instanceof ShutdownAwarePlugin) {
                            shutdownAwarePluginList.remove(plugin);
                        }
                        plugin.stop();
                    });
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get all registered plugins.
     *
     * @return plugins
     * @apiNote Be sure to avoid directly modifying returned collection instances,
     * otherwise, unexpected results may be obtained through the manager
     */
    @Override
    public Collection<ThreadPoolPlugin> getAllPlugins() {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return registeredPlugins.values();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId plugin id
     * @return ture if target has been registered, false otherwise
     */
    @Override
    public boolean isRegistered(String pluginId) {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return registeredPlugins.containsKey(pluginId);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get {@link ThreadPoolPlugin}.
     *
     * @param pluginId plugin id
     * @param <A>      plugin type
     * @return {@link ThreadPoolPlugin}, null if unregister
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends ThreadPoolPlugin> Optional<A> getPlugin(String pluginId) {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return (Optional<A>) Optional.ofNullable(registeredPlugins.get(pluginId));
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get execute plugin list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    public Collection<ExecuteAwarePlugin> getExecuteAwarePluginList() {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return executeAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get rejected plugin list.
     *
     * @return {@link RejectedAwarePlugin}
     * @apiNote Be sure to avoid directly modifying returned collection instances,
     * otherwise, unexpected results may be obtained through the manager
     */
    @Override
    public Collection<RejectedAwarePlugin> getRejectedAwarePluginList() {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return rejectedAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get shutdown plugin list.
     *
     * @return {@link ShutdownAwarePlugin}
     * @apiNote Be sure to avoid directly modifying returned collection instances,
     * otherwise, unexpected results may be obtained through the manager
     */
    @Override
    public Collection<ShutdownAwarePlugin> getShutdownAwarePluginList() {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return shutdownAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get shutdown plugin list.
     *
     * @return {@link ShutdownAwarePlugin}
     * @apiNote Be sure to avoid directly modifying returned collection instances,
     * otherwise, unexpected results may be obtained through the manager
     */
    @Override
    public Collection<TaskAwarePlugin> getTaskAwarePluginList() {
        Lock readLock = instanceLock.readLock();
        readLock.lock();
        try {
            return taskAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }
}
