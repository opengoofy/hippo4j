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

import cn.hippo4j.common.toolkit.Assert;
import lombok.NonNull;

import java.util.*;

/**
 * The default implementation of {@link ThreadPoolPluginRegistry}.
 *
 * @author huangchengxing
 */
public class DefaultThreadPoolPluginRegistry implements ThreadPoolPluginRegistry {

    /**
     * Registered {@link ThreadPoolPlugin}.
     */
    private final Map<String, ThreadPoolPlugin> registeredPlugins = new HashMap<>(16);

    /**
     * Registered {@link TaskAwarePlugin}.
     */
    private final List<TaskAwarePlugin> taskAwarePluginList = new ArrayList<>();

    /**
     * Registered {@link ExecuteAwarePlugin}.
     */
    private final List<ExecuteAwarePlugin> executeAwarePluginList = new ArrayList<>();

    /**
     * Registered {@link RejectedAwarePlugin}.
     */
    private final List<RejectedAwarePlugin> rejectedAwarePluginList = new ArrayList<>();

    /**
     * Registered {@link ShutdownAwarePlugin}.
     */
    private final List<ShutdownAwarePlugin> shutdownAwarePluginList = new ArrayList<>();

    /**
     * Clear all.
     */
    @Override
    public synchronized void clear() {
        registeredPlugins.clear();
        taskAwarePluginList.clear();
        executeAwarePluginList.clear();
        rejectedAwarePluginList.clear();
        shutdownAwarePluginList.clear();
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param aware aware
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()} already exists in the registry
     * @see ThreadPoolPlugin#getId()
     */
    @Override
    public synchronized void register(@NonNull ThreadPoolPlugin aware) {
        String id = aware.getId();
        Assert.isTrue(!isRegistered(id), "The plug-in with id [" + id + "] has been registered");

        // register aware
        registeredPlugins.put(id, aware);
        // quick index
        if (aware instanceof TaskAwarePlugin) {
            taskAwarePluginList.add((TaskAwarePlugin) aware);
        }
        if (aware instanceof ExecuteAwarePlugin) {
            executeAwarePluginList.add((ExecuteAwarePlugin) aware);
        }
        if (aware instanceof RejectedAwarePlugin) {
            rejectedAwarePluginList.add((RejectedAwarePlugin) aware);
        }
        if (aware instanceof ShutdownAwarePlugin) {
            shutdownAwarePluginList.add((ShutdownAwarePlugin) aware);
        }
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param id name
     */
    @Override
    public synchronized void unregister(String id) {
        Optional.ofNullable(id)
            .map(registeredPlugins::remove)
            .ifPresent(old -> {
                if (old instanceof TaskAwarePlugin) {
                    taskAwarePluginList.remove(old);
                }
                if (old instanceof ExecuteAwarePlugin) {
                    executeAwarePluginList.remove(old);
                }
                if (old instanceof RejectedAwarePlugin) {
                    rejectedAwarePluginList.remove(old);
                }
                if (old instanceof ShutdownAwarePlugin) {
                    shutdownAwarePluginList.remove(old);
                }
            });
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param id name
     * @return ture if target has been registered, false otherwise
     */
    @Override
    public boolean isRegistered(String id) {
        return registeredPlugins.containsKey(id);
    }

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param id target name
     * @param <A> target aware type
     * @return {@link ThreadPoolPlugin}, null if unregister
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends ThreadPoolPlugin> A getAware(String id) {
        return (A) registeredPlugins.get(id);
    }

    /**
     * Get execute aware list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    public Collection<ExecuteAwarePlugin> getExecuteAwareList() {
        return executeAwarePluginList;
    }

    /**
     * Get rejected aware list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    @Override
    public Collection<RejectedAwarePlugin> getRejectedAwareList() {
        return rejectedAwarePluginList;
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    public Collection<ShutdownAwarePlugin> getShutdownAwareList() {
        return shutdownAwarePluginList;
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    public Collection<TaskAwarePlugin> getTaskAwareList() {
        return taskAwarePluginList;
    }

}
