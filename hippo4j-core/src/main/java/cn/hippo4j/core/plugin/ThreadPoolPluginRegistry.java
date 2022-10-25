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

package cn.hippo4j.core.plugin;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Registry of {@link ThreadPoolPlugin}
 *
 * @author huangchengxing
 */
public interface ThreadPoolPluginRegistry {

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
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin plugin
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()}
     *                                  already exists in the registry
     * @see ThreadPoolPlugin#getId()
     */
    void register(ThreadPoolPlugin plugin);

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId plugin id
     * @return ture if target has been registered, false otherwise
     */
    boolean isRegistered(String pluginId);

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param pluginId plugin id
     */
    void unregister(String pluginId);

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param pluginId  plugin id
     * @param <A> target aware type
     * @return {@link ThreadPoolPlugin}, null if unregister
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    @Nullable
    <A extends ThreadPoolPlugin> A getPlugin(String pluginId);

    /**
     * Get execute aware plugin list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    Collection<ExecuteAwarePlugin> getExecuteAwareList();

    /**
     * Get rejected aware plugin list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    Collection<RejectedAwarePlugin> getRejectedAwareList();

    /**
     * Get shutdown aware plugin list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    Collection<ShutdownAwarePlugin> getShutdownAwareList();

    /**
     * Get shutdown aware plugin list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    Collection<TaskAwarePlugin> getTaskAwareList();

    /**
     * Try to get target plugin and apply operation, do nothing if it's not present.
     *
     * @param pluginId plugin id
     * @param targetType target type
     * @param consumer operation for target plugin
     * @param <A> plugin type
     * @return this instance
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    default <A extends ThreadPoolPlugin> ThreadPoolPluginRegistry getAndThen(
        String pluginId, Class<A> targetType, Consumer<A> consumer) {
        Optional.ofNullable(getPlugin(pluginId))
            .map(targetType::cast)
            .ifPresent(consumer);
        return this;
    }

    /**
     * Try to get target plugin and return value of apply function, return default value if it's not present.
     *
     * @param pluginId plugin id
     * @param targetType target type
     * @param function operation for target plugin
     * @param defaultValue default value
     * @param <A> plugin type
     * @return value of apply function, default value if plugin is not present
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    default <A extends ThreadPoolPlugin, R> R getAndThen(String pluginId, Class<A> targetType, Function<A, R> function, R defaultValue) {
        return Optional.ofNullable(getPlugin(pluginId))
            .map(targetType::cast)
            .map(function)
            .orElse(defaultValue);
    }

}
