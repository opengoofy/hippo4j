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

package cn.hippo4j.core.executor.plugin.impl;

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.TaskAwarePlugin;
import cn.hippo4j.core.executor.plugin.PluginRuntime;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.core.task.TaskDecorator;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorate tasks when they are submitted to thread-pool.
 */
public class TaskDecoratorPlugin implements TaskAwarePlugin {

    public static final String PLUGIN_NAME = TaskDecoratorPlugin.class.getSimpleName();

    /**
     * Decorators
     */
    @Getter
    private final List<TaskDecorator> decorators = new ArrayList<>();

    /**
     * Callback when task is executed.
     *
     * @param runnable runnable
     * @return tasks to be execute
     * @see ExtensibleThreadPoolExecutor#execute
     */
    @Override
    public Runnable beforeTaskExecute(@NonNull Runnable runnable) {
        for (TaskDecorator decorator : decorators) {
            runnable = decorator.decorate(runnable);
        }
        return runnable;
    }

    /**
     * Get plugin runtime info.
     *
     * @return plugin runtime info
     */
    @Override
    public PluginRuntime getPluginRuntime() {
        PluginRuntime runtime = new PluginRuntime(getId());
        for (int i = 0; i < decorators.size(); i++) {
            TaskDecorator decorator = decorators.get(i);
            runtime.addInfo("decorator" + i, decorator.getClass().getName());
        }
        return runtime;
    }

    /**
     * Add a decorator.
     *
     * @param decorator decorator
     */
    public void addDecorator(@NonNull TaskDecorator decorator) {
        decorators.remove(decorator);
        decorators.add(decorator);
    }

    /**
     * Clear all decorators.
     */
    public void clearDecorators() {
        decorators.clear();
    }

    /**
     * Remove decorators.
     */
    public void removeDecorator(TaskDecorator decorator) {
        decorators.remove(decorator);
    }
}
