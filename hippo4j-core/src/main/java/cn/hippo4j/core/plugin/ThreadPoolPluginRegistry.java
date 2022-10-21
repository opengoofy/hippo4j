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
     * Register a {@link ThreadPoolPlugin}
     *
     * @param aware aware
     */
    void register(ThreadPoolPlugin aware);

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param name name
     * @return ture if target has been registered, false otherwise
     */
    boolean isRegistered(String name);

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param name name
     */
    void unregister(String name);

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param name target name
     * @param <A> target aware type
     * @return {@link ThreadPoolPlugin}, null if unregister
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    <A extends ThreadPoolPlugin> A getAware(String name);

    /**
     * Get execute aware list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    Collection<ExecuteAwarePlugin> getExecuteAwareList();

    /**
     * Get rejected aware list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    Collection<RejectedAwarePlugin> getRejectedAwareList();

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    Collection<ShutdownAwarePlugin> getShutdownAwareList();

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    Collection<TaskAwarePlugin> getTaskAwareList();

    /**
     * Try to get target Aware and apply operation, do nothing if is not present.
     *
     * @param name aware name
     * @param targetType target type
     * @param consumer operation for target aware
     * @param <A> aware type
     * @return this instance
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    default <A extends ThreadPoolPlugin> ThreadPoolPluginRegistry getAndThen(
        String name, Class<A> targetType, Consumer<A> consumer) {
        Optional.ofNullable(getAware(name))
            .map(targetType::cast)
            .ifPresent(consumer);
        return this;
    }

    /**
     * Try to get target Aware and return value of apply function, return default value if is not present.
     *
     * @param name aware name
     * @param targetType target type
     * @param function operation for target aware
     * @param defaultValue default value
     * @param <A> aware type
     * @return value of apply function, default value if aware is not present
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    default <A extends ThreadPoolPlugin, R> R getAndThen(String name, Class<A> targetType, Function<A, R> function, R defaultValue) {
        return Optional.ofNullable(getAware(name))
            .map(targetType::cast)
            .map(function)
            .orElse(defaultValue);
    }

}
