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

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.PluginRuntime;
import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;
import cn.hippo4j.core.executor.plugin.impl.TaskDecoratorPlugin;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link TaskDecoratorPlugin}
 */
public class TaskDecoratorPluginTest {

    private final AtomicInteger taskExecuteCount = new AtomicInteger(0);

    @Test
    public void testGetId() {
        Assert.assertEquals(TaskDecoratorPlugin.PLUGIN_NAME, new TaskDecoratorPlugin().getId());
    }

    @Test
    public void testGetRuntime() {
        ThreadPoolPlugin plugin = new TaskDecoratorPlugin();
        PluginRuntime runtime = new TaskDecoratorPlugin().getPluginRuntime();
        Assert.assertNotNull(runtime);
        Assert.assertEquals(plugin.getId(), runtime.getPluginId());
    }

    @Test
    public void testAddDecorator() {
        TaskDecoratorPlugin plugin = new TaskDecoratorPlugin();
        plugin.addDecorator(runnable -> runnable);
        plugin.addDecorator(runnable -> runnable);
        Assert.assertEquals(2, plugin.getDecorators().size());
    }

    @Test
    public void testRemoveDecorator() {
        TaskDecoratorPlugin plugin = new TaskDecoratorPlugin();
        TaskDecorator decorator = runnable -> runnable;
        plugin.addDecorator(decorator);
        plugin.removeDecorator(decorator);
        Assert.assertTrue(plugin.getDecorators().isEmpty());
    }

    @Test
    public void testClear() {
        TaskDecoratorPlugin plugin = new TaskDecoratorPlugin();
        TaskDecorator decorator = runnable -> runnable;
        plugin.addDecorator(decorator);
        plugin.addDecorator(decorator);
        plugin.clearDecorators();
        Assert.assertTrue(plugin.getDecorators().isEmpty());
    }

    @Test
    public void testBeforeTaskExecute() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                5, 5, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        TaskDecoratorPlugin plugin = new TaskDecoratorPlugin();
        plugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        plugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        executor.register(plugin);
        executor.execute(() -> {
        });
        ThreadUtil.sleep(500L);
        Assert.assertEquals(2, taskExecuteCount.get());
    }

}
