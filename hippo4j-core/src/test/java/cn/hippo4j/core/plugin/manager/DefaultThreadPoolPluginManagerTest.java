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

import cn.hippo4j.core.plugin.*;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test for {@link DefaultThreadPoolPluginManager}
 */
public class DefaultThreadPoolPluginManagerTest {

    private DefaultThreadPoolPluginManager manager;

    @Before
    public void initRegistry() {
        manager = new DefaultThreadPoolPluginManager();
    }

    @Test
    public void testRegister() {
        manager.register(new TestShutdownAwarePlugin());
        Assert.assertEquals(1, manager.getAllPlugins().size());
    }

    @Test
    public void testGetAllPlugins() {
        manager.register(new TestExecuteAwarePlugin());
        manager.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(2, manager.getAllPlugins().size());
    }

    @Test
    public void testClear() {
        manager.register(new TestExecuteAwarePlugin());
        manager.clear();
        Assert.assertTrue(manager.getAllPlugins().isEmpty());
    }

    @Test
    public void testTryRegister() {
        Assert.assertTrue(manager.tryRegister(new TestExecuteAwarePlugin()));
        Assert.assertFalse(manager.tryRegister(new TestExecuteAwarePlugin()));
    }

    @Test
    public void testIsRegistered() {
        Assert.assertFalse(manager.isRegistered(TestExecuteAwarePlugin.class.getSimpleName()));
        manager.register(new TestExecuteAwarePlugin());
        Assert.assertTrue(manager.isRegistered(TestExecuteAwarePlugin.class.getSimpleName()));
    }

    @Test
    public void testUnregister() {
        manager.register(new TestExecuteAwarePlugin());
        manager.unregister(TestExecuteAwarePlugin.class.getSimpleName());
        Assert.assertFalse(manager.isRegistered(TestExecuteAwarePlugin.class.getSimpleName()));
    }

    @Test
    public void testGetPlugin() {
        ThreadPoolPlugin plugin = new TestExecuteAwarePlugin();
        manager.register(plugin);
        Assert.assertSame(plugin, manager.getPlugin(plugin.getId()).orElse(null));
    }

    @Test
    public void testGetRejectedAwarePluginList() {
        manager.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(1, manager.getRejectedAwarePluginList().size());
    }

    @Test
    public void testGetShutdownAwarePluginList() {
        manager.register(new TestShutdownAwarePlugin());
        Assert.assertEquals(1, manager.getShutdownAwarePluginList().size());
    }

    @Test
    public void testGetTaskAwarePluginList() {
        manager.register(new TestTaskAwarePlugin());
        Assert.assertEquals(1, manager.getTaskAwarePluginList().size());
    }

    @Test
    public void testGetExecuteAwarePluginList() {
        manager.register(new TestExecuteAwarePlugin());
        Assert.assertEquals(1, manager.getExecuteAwarePluginList().size());
    }

    @Test
    public void testGetAllPluginsOfType() {
        manager.register(new TestExecuteAwarePlugin());
        manager.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(1, manager.getAllPluginsOfType(TestExecuteAwarePlugin.class).size());
        Assert.assertEquals(1, manager.getAllPluginsOfType(TestRejectedAwarePlugin.class).size());
        Assert.assertEquals(2, manager.getAllPluginsOfType(ThreadPoolPlugin.class).size());
    }

    @Test
    public void testGetAllPluginRuntimes() {
        manager.register(new TestExecuteAwarePlugin());
        manager.register(new TestRejectedAwarePlugin());
        Assert.assertEquals(2, manager.getAllPluginRuntimes().size());
    }

    @Test
    public void testGetPluginRuntime() {
        manager.register(new TestExecuteAwarePlugin());
        Assert.assertTrue(manager.getRuntime(TestExecuteAwarePlugin.class.getSimpleName()).isPresent());
    }

    @Test
    public void testGetPluginOfType() {
        manager.register(new TestExecuteAwarePlugin());
        Assert.assertTrue(manager.getPluginOfType(TestExecuteAwarePlugin.class.getSimpleName(), TestExecuteAwarePlugin.class).isPresent());
        Assert.assertTrue(manager.getPluginOfType(TestExecuteAwarePlugin.class.getSimpleName(), ThreadPoolPlugin.class).isPresent());
        Assert.assertFalse(manager.getPluginOfType(TestExecuteAwarePlugin.class.getSimpleName(), RejectedAwarePlugin.class).isPresent());
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
