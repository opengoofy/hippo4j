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
import cn.hippo4j.core.executor.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.executor.plugin.RejectedAwarePlugin;
import cn.hippo4j.core.executor.plugin.ShutdownAwarePlugin;
import cn.hippo4j.core.executor.plugin.TaskAwarePlugin;
import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for default method of {@link ThreadPoolPluginSupport}
 */
public class ThreadPoolPluginSupportTest {

    private final ThreadPoolPluginManager manager = new DefaultThreadPoolPluginManager();
    private final ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", manager,
            5, 5, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.AbortPolicy());
    private final ThreadPoolPluginSupport support = new TestSupport(executor.getThreadPoolId(), executor, manager);

    @Test
    public void testGetThreadPoolId() {
        Assert.assertEquals(executor.getThreadPoolId(), support.getThreadPoolId());
    }

    @Test
    public void testGetThreadPoolPluginManager() {
        Assert.assertEquals(manager, support.getThreadPoolPluginManager());
    }

    @Getter
    @RequiredArgsConstructor
    private static class TestSupport implements ThreadPoolPluginSupport {

        private final String threadPoolId;
        private final ExtensibleThreadPoolExecutor threadPoolExecutor;
        private final ThreadPoolPluginManager threadPoolPluginManager;
    }

    // ================ default delegate method ================

    @Test
    public void testRegister() {
        support.register(new TestShutdownAwarePlugin());
        Assert.assertEquals(1, support.getAllPlugins().size());
    }

    @Test
    public void testGetAllPlugins() {
        support.register(new TestExecuteAwarePlugin());
        support.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(2, support.getAllPlugins().size());
    }

    @Test
    public void testClear() {
        support.register(new TestExecuteAwarePlugin());
        support.clear();
        Assert.assertTrue(support.getAllPlugins().isEmpty());
    }

    @Test
    public void testTryRegister() {
        Assert.assertTrue(support.tryRegister(new TestExecuteAwarePlugin()));
        Assert.assertFalse(support.tryRegister(new TestExecuteAwarePlugin()));
    }

    @Test
    public void testIsRegistered() {
        Assert.assertFalse(support.isRegistered(TestExecuteAwarePlugin.class.getSimpleName()));
        support.register(new TestExecuteAwarePlugin());
        Assert.assertTrue(support.isRegistered(TestExecuteAwarePlugin.class.getSimpleName()));
    }

    @Test
    public void testUnregister() {
        support.register(new TestExecuteAwarePlugin());
        support.unregister(TestExecuteAwarePlugin.class.getSimpleName());
        Assert.assertFalse(support.isRegistered(TestExecuteAwarePlugin.class.getSimpleName()));
    }

    @Test
    public void testGetPlugin() {
        ThreadPoolPlugin plugin = new TestExecuteAwarePlugin();
        support.register(plugin);
        Assert.assertSame(plugin, support.getPlugin(plugin.getId()).orElse(null));
    }

    @Test
    public void testGetRejectedAwarePluginList() {
        support.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(1, support.getRejectedAwarePluginList().size());
    }

    @Test
    public void testGetShutdownAwarePluginList() {
        support.register(new TestShutdownAwarePlugin());
        Assert.assertEquals(1, support.getShutdownAwarePluginList().size());
    }

    @Test
    public void testGetTaskAwarePluginList() {
        support.register(new TestTaskAwarePlugin());
        Assert.assertEquals(1, support.getTaskAwarePluginList().size());
    }

    @Test
    public void testGetExecuteAwarePluginList() {
        support.register(new TestExecuteAwarePlugin());
        Assert.assertEquals(1, support.getExecuteAwarePluginList().size());
    }

    @Test
    public void testGetAllPluginsOfType() {
        support.register(new TestExecuteAwarePlugin());
        support.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(1, support.getAllPluginsOfType(TestExecuteAwarePlugin.class).size());
        Assert.assertEquals(1, support.getAllPluginsOfType(TestRejectedAwarePlugin.class).size());
        Assert.assertEquals(2, support.getAllPluginsOfType(ThreadPoolPlugin.class).size());
    }

    @Test
    public void testGetAllPluginRuntimes() {
        support.register(new TestExecuteAwarePlugin());
        support.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(2, support.getAllPluginRuntimes().size());
    }

    @Test
    public void testGetPluginRuntime() {
        support.register(new TestExecuteAwarePlugin());
        Assert.assertTrue(support.getRuntime(TestExecuteAwarePlugin.class.getSimpleName()).isPresent());
    }

    @Test
    public void testGetPluginOfType() {
        support.register(new TestExecuteAwarePlugin());
        Assert.assertTrue(support.getPluginOfType(TestExecuteAwarePlugin.class.getSimpleName(), TestExecuteAwarePlugin.class).isPresent());
        Assert.assertTrue(support.getPluginOfType(TestExecuteAwarePlugin.class.getSimpleName(), ThreadPoolPlugin.class).isPresent());
        Assert.assertFalse(support.getPluginOfType(TestExecuteAwarePlugin.class.getSimpleName(), RejectedAwarePlugin.class).isPresent());
    }

    @Test
    public void testEnable() {
        ThreadPoolPlugin plugin = new TestExecuteAwarePlugin();
        Assert.assertFalse(support.enable(plugin.getId()));
        support.register(plugin);
        Assert.assertFalse(support.enable(plugin.getId()));
        support.disable(plugin.getId());
        Assert.assertTrue(support.enable(plugin.getId()));
    }

    @Test
    public void testDisable() {
        ThreadPoolPlugin plugin = new TestExecuteAwarePlugin();
        Assert.assertFalse(support.disable(plugin.getId()));

        support.register(plugin);
        Assert.assertTrue(support.disable(plugin.getId()));
        Assert.assertFalse(support.disable(plugin.getId()));

        Assert.assertTrue(support.getExecuteAwarePluginList().isEmpty());
        Assert.assertEquals(1, support.getAllPlugins().size());
    }

    @Test
    public void testIsDisable() {
        ThreadPoolPlugin plugin = new TestExecuteAwarePlugin();
        Assert.assertFalse(support.isDisabled(plugin.getId()));

        support.register(plugin);
        Assert.assertTrue(support.disable(plugin.getId()));
        Assert.assertTrue(support.isDisabled(plugin.getId()));
    }

    @Test
    public void testGetDisabledPluginIds() {
        ThreadPoolPlugin plugin = new TestExecuteAwarePlugin();
        Assert.assertTrue(support.getAllDisabledPluginIds().isEmpty());

        support.register(plugin);
        Assert.assertTrue(support.disable(plugin.getId()));
        Assert.assertEquals(1, support.getAllDisabledPluginIds().size());
    }

    @Getter
    private final static class TestTaskAwarePlugin implements TaskAwarePlugin {

        private final String id = this.getClass().getSimpleName();
    }

    @Getter
    private final static class TestExecuteAwarePlugin implements ExecuteAwarePlugin {

        private final String id = this.getClass().getSimpleName();
    }

    @Getter
    private final static class TestRejectedAwarePlugin implements RejectedAwarePlugin {

        private final String id = this.getClass().getSimpleName();
    }

    @Getter
    private final static class TestShutdownAwarePlugin implements ShutdownAwarePlugin {

        private final String id = this.getClass().getSimpleName();
    }

}
