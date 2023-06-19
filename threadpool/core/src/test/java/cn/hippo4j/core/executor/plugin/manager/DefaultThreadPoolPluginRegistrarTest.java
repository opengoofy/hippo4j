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

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.impl.TaskDecoratorPlugin;
import cn.hippo4j.core.executor.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.executor.plugin.impl.TaskRejectNotifyAlarmPlugin;
import cn.hippo4j.core.executor.plugin.impl.TaskTimeoutNotifyAlarmPlugin;
import cn.hippo4j.core.executor.plugin.impl.ThreadPoolExecutorShutdownPlugin;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginRegistrar;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginRegistrar;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link DefaultThreadPoolPluginRegistrar}
 */
public class DefaultThreadPoolPluginRegistrarTest {

    @Test
    public void testGetId() {
        ThreadPoolPluginRegistrar registrar = new DefaultThreadPoolPluginRegistrar();
        Assert.assertEquals(registrar.getClass().getSimpleName(), registrar.getId());
    }

    @Test
    public void testDoRegister() {
        ThreadPoolPluginRegistrar registrar = new DefaultThreadPoolPluginRegistrar(100L, 100L);
        ThreadPoolPluginManager manager = new DefaultThreadPoolPluginManager();
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", manager,
                5, 5, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.AbortPolicy());
        registrar.doRegister(executor);

        Assert.assertTrue(manager.getPlugin(TaskDecoratorPlugin.PLUGIN_NAME).isPresent());
        Assert.assertTrue(manager.getPlugin(TaskTimeoutNotifyAlarmPlugin.PLUGIN_NAME).isPresent());
        Assert.assertTrue(manager.getPlugin(TaskRejectCountRecordPlugin.PLUGIN_NAME).isPresent());
        Assert.assertTrue(manager.getPlugin(TaskRejectNotifyAlarmPlugin.PLUGIN_NAME).isPresent());
        Assert.assertTrue(manager.getPlugin(ThreadPoolExecutorShutdownPlugin.PLUGIN_NAME).isPresent());
    }

}
