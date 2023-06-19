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

import cn.hippo4j.core.executor.plugin.manager.EmptyThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginManager;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

/**
 * test for {@link EmptyThreadPoolPluginManager}
 */
public class EmptyThreadPoolPluginManagerTest {

    private final ThreadPoolPluginManager manager = EmptyThreadPoolPluginManager.INSTANCE;

    @Test
    public void testEmpty() {
        Assert.assertSame(manager, ThreadPoolPluginManager.empty());
    }

    @Test
    public void testGetAllPlugins() {
        Assert.assertEquals(Collections.emptyList(), manager.getAllPluginRuntimes());
    }

    @Test
    public void testClear() {
        manager.clear();
        Assert.assertTrue(isEmpty(manager));
    }

    @Test
    public void testRegister() {
        manager.register(new TestPlugin());
        Assert.assertTrue(isEmpty(manager));
    }

    @Test
    public void testTryRegister() {
        Assert.assertFalse(manager.tryRegister(new TestPlugin()));
    }

    @Test
    public void testIsRegistered() {
        manager.register(new TestPlugin());
        Assert.assertFalse(manager.isRegistered(TestPlugin.class.getSimpleName()));
    }

    @Test
    public void testUnregister() {
        manager.register(new TestPlugin());
        manager.unregister(TestPlugin.class.getSimpleName());
        Assert.assertTrue(isEmpty(manager));
    }

    @Test
    public void testGetPlugin() {
        Assert.assertSame(Optional.empty(), manager.getPlugin(""));
    }

    @Test
    public void testGetRejectedAwarePluginList() {
        Assert.assertEquals(Collections.emptyList(), manager.getRejectedAwarePluginList());
    }

    @Test
    public void testGetShutdownAwarePluginList() {
        Assert.assertEquals(Collections.emptyList(), manager.getShutdownAwarePluginList());
    }

    @Test
    public void testGetTaskAwarePluginList() {
        Assert.assertEquals(Collections.emptyList(), manager.getTaskAwarePluginList());
    }

    @Test
    public void testGetExecuteAwarePluginList() {
        Assert.assertEquals(Collections.emptyList(), manager.getExecuteAwarePluginList());
    }

    @Test
    public void testEnable() {
        ThreadPoolPlugin plugin = new TestPlugin();
        Assert.assertFalse(manager.enable(plugin.getId()));
        manager.register(plugin);
        Assert.assertFalse(manager.enable(plugin.getId()));
        manager.disable(plugin.getId());
        Assert.assertFalse(manager.enable(plugin.getId()));
    }

    @Test
    public void testDisable() {
        ThreadPoolPlugin plugin = new TestPlugin();
        Assert.assertFalse(manager.disable(plugin.getId()));

        manager.register(plugin);
        Assert.assertFalse(manager.disable(plugin.getId()));
        Assert.assertFalse(manager.disable(plugin.getId()));

        Assert.assertTrue(manager.getExecuteAwarePluginList().isEmpty());
        Assert.assertTrue(manager.getAllPlugins().isEmpty());
    }

    @Test
    public void testIsDisable() {
        ThreadPoolPlugin plugin = new TestPlugin();
        Assert.assertTrue(manager.isDisabled(plugin.getId()));

        manager.register(plugin);
        Assert.assertFalse(manager.disable(plugin.getId()));
        Assert.assertTrue(manager.isDisabled(plugin.getId()));
    }

    @Test
    public void testGetDisabledPluginIds() {
        ThreadPoolPlugin plugin = new TestPlugin();
        Assert.assertTrue(manager.getAllDisabledPluginIds().isEmpty());

        manager.register(plugin);
        Assert.assertFalse(manager.disable(plugin.getId()));
        Assert.assertTrue(manager.getAllDisabledPluginIds().isEmpty());
    }

    private static boolean isEmpty(ThreadPoolPluginManager manager) {
        return manager.getAllPlugins().isEmpty();
    }

    @Getter
    private static class TestPlugin implements ThreadPoolPlugin {

        private final String id = TestPlugin.class.getSimpleName();
    }

}
