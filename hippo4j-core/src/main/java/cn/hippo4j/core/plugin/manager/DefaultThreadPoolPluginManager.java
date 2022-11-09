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
import cn.hippo4j.core.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.plugin.RejectedAwarePlugin;
import cn.hippo4j.core.plugin.ShutdownAwarePlugin;
import cn.hippo4j.core.plugin.TaskAwarePlugin;
import cn.hippo4j.core.plugin.ThreadPoolPlugin;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>The default implementation of {@link ThreadPoolPluginManager}.
 * Provide basic {@link ThreadPoolPlugin} registration, logout and acquisition functions.
 * Most APIs ensure limited thread-safe.
 *
 * <p>Usually registered to {@link cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor},
 * or bound to an {@link java.util.concurrent.ThreadPoolExecutor} instance through {@link ThreadPoolPluginSupport}
 * to support its plugin based extension functions.
 *
 * <p>When {@link #isEnableSort()} is true, plugins can be obtained in batches
 * in the order specified by {@link AnnotationAwareOrderComparator}.<br />
 * When the sorting function is enabled through {@link #setEnableSort} for the first time,
 * all registered plugins will be sorted,
 * Later, whenever a new plug-in is registered, all plug-ins will be reordered again.
 *
 * <p><b>NOTE:</b>
 * When the list of plugins is obtained through the {@code getXXX} method of manager, the list is not immutable.
 * This means that until actually start iterating over the list,
 * registering or unregistering plugins through the manager will affect the results of the iteration.
 * Therefore, we should try to ensure that <b>get the latest plugin list from the manager before each use</b>.
 *
 * @see cn.hippo4j.core.executor.DynamicThreadPoolExecutor
 * @see cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor
 * @see AnnotationAwareOrderComparator
 */
public class DefaultThreadPoolPluginManager implements ThreadPoolPluginManager {

    /**
     * Lock of this instance
     */
    private final ReadWriteLockSupport mainLock = new ReadWriteLockSupport(new ReentrantReadWriteLock());

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
     * Enable sort.
     */
    @Getter
    private boolean enableSort = false;

    /**
     * Clear all.
     */
    @Override
    public synchronized void clear() {
        mainLock.runWithWriteLock(() -> {
            Collection<ThreadPoolPlugin> plugins = registeredPlugins.values();
            registeredPlugins.clear();
            taskAwarePluginList.clear();
            executeAwarePluginList.clear();
            rejectedAwarePluginList.clear();
            shutdownAwarePluginList.clear();
            plugins.forEach(ThreadPoolPlugin::stop);
        });
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin plugin
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()} already exists in the registry
     * @see ThreadPoolPlugin#getId()
     * @see #isEnableSort
     * @see AnnotationAwareOrderComparator#sort(List)
     */
    @Override
    public void register(@NonNull ThreadPoolPlugin plugin) {
        mainLock.runWithWriteLock(() -> {
            String id = plugin.getId();
            Assert.isTrue(!isRegistered(id), "The plugin with id [" + id + "] has been registered");
            registeredPlugins.put(id, plugin);
            if (plugin instanceof TaskAwarePlugin) {
                taskAwarePluginList.add((TaskAwarePlugin) plugin);
                if (enableSort) {
                    AnnotationAwareOrderComparator.sort(taskAwarePluginList);
                }
            }
            if (plugin instanceof ExecuteAwarePlugin) {
                executeAwarePluginList.add((ExecuteAwarePlugin) plugin);
                if (enableSort) {
                    AnnotationAwareOrderComparator.sort(executeAwarePluginList);
                }
            }
            if (plugin instanceof RejectedAwarePlugin) {
                rejectedAwarePluginList.add((RejectedAwarePlugin) plugin);
                if (enableSort) {
                    AnnotationAwareOrderComparator.sort(rejectedAwarePluginList);
                }
            }
            if (plugin instanceof ShutdownAwarePlugin) {
                shutdownAwarePluginList.add((ShutdownAwarePlugin) plugin);
                if (enableSort) {
                    AnnotationAwareOrderComparator.sort(shutdownAwarePluginList);
                }
            }
            plugin.start();
        });
    }

    /**
     * Register plugin if it's not registered.
     *
     * @param plugin plugin
     * @return return true if successful register new plugin, false otherwise
     */
    @Override
    public boolean tryRegister(ThreadPoolPlugin plugin) {
        return mainLock.applyWithWriteLock(() -> {
            if (registeredPlugins.containsKey(plugin.getId())) {
                return false;
            }
            register(plugin);
            return true;
        });
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param pluginId plugin id
     */
    @Override
    public void unregister(String pluginId) {
        mainLock.runWithWriteLock(
                () -> Optional.ofNullable(pluginId)
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
                        }));
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
        return mainLock.applyWithReadLock(() -> {
            // sort if necessary
            if (enableSort) {
                return registeredPlugins.values().stream()
                        .sorted(AnnotationAwareOrderComparator.INSTANCE)
                        .collect(Collectors.toList());
            }
            return registeredPlugins.values();
        });
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId plugin id
     * @return ture if target has been registered, false otherwise
     */
    @Override
    public boolean isRegistered(String pluginId) {
        return mainLock.applyWithReadLock(() -> registeredPlugins.containsKey(pluginId));
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
        return mainLock.applyWithReadLock(
                () -> (Optional<A>) Optional.ofNullable(registeredPlugins.get(pluginId)));
    }

    /**
     * Get execute plugin list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    public Collection<ExecuteAwarePlugin> getExecuteAwarePluginList() {
        return mainLock.applyWithReadLock(() -> executeAwarePluginList);
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
        return mainLock.applyWithReadLock(() -> rejectedAwarePluginList);
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
        return mainLock.applyWithReadLock(() -> shutdownAwarePluginList);
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
        return mainLock.applyWithReadLock(() -> taskAwarePluginList);
    }

    /**
     * <p>Set whether sorting is allowed. <br />
     * <b>NOTE</b>:
     * If {@link #isEnableSort} returns false and {@code enableSort} is true,
     * All currently registered plug-ins will be reordered immediately.
     *
     * @param enableSort enable sort
     * @return {@link DefaultThreadPoolPluginManager}
     * @see AnnotationAwareOrderComparator#sort(List)
     */
    public DefaultThreadPoolPluginManager setEnableSort(boolean enableSort) {
        // if it was unordered before, it needs to be reordered now
        if (!isEnableSort() && enableSort) {
            mainLock.runWithWriteLock(() -> {
                // if it has been successfully updated, there is no need to operate again
                if (this.enableSort != enableSort) {
                    AnnotationAwareOrderComparator.sort(taskAwarePluginList);
                    AnnotationAwareOrderComparator.sort(executeAwarePluginList);
                    AnnotationAwareOrderComparator.sort(rejectedAwarePluginList);
                    AnnotationAwareOrderComparator.sort(shutdownAwarePluginList);
                }
            });
        }
        this.enableSort = enableSort;
        return this;
    }

    /**
     * Read write lock support.
     */
    @RequiredArgsConstructor
    private static class ReadWriteLockSupport {

        /**
         * lock
         */
        private final ReadWriteLock lock;

        /**
         * Get the read-lock and do something.
         *
         * @param supplier supplier
         */
        public <T> T applyWithReadLock(Supplier<T> supplier) {
            Lock readLock = lock.readLock();
            readLock.lock();
            try {
                return supplier.get();
            } finally {
                readLock.unlock();
            }
        }

        /**
         * Get the write-lock and do something.
         *
         * @param runnable runnable
         */
        public void runWithWriteLock(Runnable runnable) {
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                runnable.run();
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * Get the write-lock and do something.
         *
         * @param supplier supplier
         */
        public <T> T applyWithWriteLock(Supplier<T> supplier) {
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                return supplier.get();
            } finally {
                writeLock.unlock();
            }
        }

    }
}
