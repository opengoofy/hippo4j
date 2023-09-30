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

import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;
import cn.hippo4j.core.executor.plugin.manager.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link DefaultGlobalThreadPoolPluginManager}
 */
public class DefaultGlobalThreadPoolPluginManagerTest {

    @Test
    public void testDoRegister() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPlugin(new TestPlugin("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("2"));
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));

        TestSupport support = new TestSupport("1");
        manager.doRegister(support);
        Assert.assertEquals(3, support.getAllPlugins().size());
    }

    @Test
    public void testRegisterThreadPoolPluginSupport() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        Assert.assertTrue(manager.enableThreadPoolPlugin(new TestPlugin("1")));

        TestSupport support = new TestSupport("1");
        Assert.assertTrue(manager.registerThreadPoolPluginSupport(support));
        Assert.assertFalse(manager.registerThreadPoolPluginSupport(support));
        Assert.assertEquals(1, support.getAllPlugins().size());

        // incremental update
        manager.enableThreadPoolPlugin(new TestPlugin("2"));
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        Assert.assertEquals(3, support.getAllPlugins().size());
    }

    @Test
    public void testCancelManagement() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPlugin(new TestPlugin("1"));

        TestSupport support = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support);
        Assert.assertEquals(1, support.getAllPlugins().size());

        manager.cancelManagement(support.getThreadPoolId());
        manager.enableThreadPoolPlugin(new TestPlugin("2"));
        Assert.assertEquals(1, support.getAllPlugins().size());
    }

    @Test
    public void testGetManagedThreadPoolPluginSupport() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();

        TestSupport support = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support);
        Assert.assertSame(support, manager.getManagedThreadPoolPluginSupport(support.getThreadPoolId()));

        support = new TestSupport("2");
        manager.registerThreadPoolPluginSupport(support);
        Assert.assertSame(support, manager.getManagedThreadPoolPluginSupport(support.getThreadPoolId()));
    }

    @Test
    public void testGetAllManagedThreadPoolPluginSupports() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.registerThreadPoolPluginSupport(new TestSupport("1"));
        manager.registerThreadPoolPluginSupport(new TestSupport("2"));
        Assert.assertEquals(2, manager.getAllManagedThreadPoolPluginSupports().size());
    }

    @Test
    public void testEnableThreadPoolPlugin() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        TestSupport support1 = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support1);
        TestSupport support2 = new TestSupport("2");
        manager.registerThreadPoolPluginSupport(support2);

        Assert.assertTrue(manager.enableThreadPoolPlugin(new TestPlugin("1")));
        Assert.assertFalse(manager.enableThreadPoolPlugin(new TestPlugin("1")));
        Assert.assertEquals(1, support1.getAllPlugins().size());
    }

    @Test
    public void testGetAllEnableThreadPoolPlugins() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPlugin(new TestPlugin("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("2"));
        Assert.assertEquals(2, manager.getAllEnableThreadPoolPlugins().size());
    }

    @Test
    public void testDisableThreadPoolPlugin() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPlugin(new TestPlugin("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("2"));
        manager.disableThreadPoolPlugin("2");
        Assert.assertEquals(1, manager.getAllEnableThreadPoolPlugins().size());
    }

    @Test
    public void testEnableThreadPoolPluginRegistrar() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        Assert.assertTrue(manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1")));
        Assert.assertFalse(manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1")));
        Assert.assertEquals(1, manager.getAllEnableThreadPoolPluginRegistrar().size());
    }

    @Test
    public void testGetAllEnableThreadPoolPluginRegistrar() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("2"));
        Assert.assertEquals(2, manager.getAllEnableThreadPoolPluginRegistrar().size());
    }

    @Test
    public void testDisableThreadPoolPluginRegistrar() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("2"));
        manager.disableThreadPoolPluginRegistrar("2");
        Assert.assertEquals(1, manager.getAllEnableThreadPoolPluginRegistrar().size());
    }

    @Test
    public void testGetAllPluginsFromManagers() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("1"));

        TestSupport support1 = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support1);
        TestSupport support2 = new TestSupport("2");
        manager.registerThreadPoolPluginSupport(support2);

        Assert.assertEquals(4, manager.getAllPluginsFromManagers().size());
    }

    @Test
    public void testGetPluginsOfTypeFromManagers() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("1"));

        TestSupport support1 = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support1);
        TestSupport support2 = new TestSupport("2");
        manager.registerThreadPoolPluginSupport(support2);

        Assert.assertEquals(4, manager.getPluginsOfTypeFromManagers(TestPlugin.class).size());
    }

    @Test
    public void testGetPluginsFromManagers() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("1"));

        TestSupport support1 = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support1);
        TestSupport support2 = new TestSupport("2");
        manager.registerThreadPoolPluginSupport(support2);

        Assert.assertEquals(2, manager.getPluginsFromManagers("1").size());
    }

    @Test
    public void testUnregisterForAllManagers() {
        GlobalThreadPoolPluginManager manager = new DefaultGlobalThreadPoolPluginManager();
        manager.enableThreadPoolPluginRegistrar(new TestRegistrar("1"));
        manager.enableThreadPoolPlugin(new TestPlugin("1"));

        TestSupport support1 = new TestSupport("1");
        manager.registerThreadPoolPluginSupport(support1);
        TestSupport support2 = new TestSupport("2");
        manager.registerThreadPoolPluginSupport(support2);

        manager.unregisterForAllManagers("1");
        Assert.assertEquals(2, manager.getAllPluginsFromManagers().size());
    }

    @RequiredArgsConstructor
    @Getter
    private static class TestSupport implements ThreadPoolPluginSupport {

        private final String threadPoolId;
        private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        private final ThreadPoolPluginManager threadPoolPluginManager = new DefaultThreadPoolPluginManager();
    }

    @Getter
    @RequiredArgsConstructor
    private static class TestRegistrar implements ThreadPoolPluginRegistrar {

        private final String id;
        @Override
        public void doRegister(ThreadPoolPluginSupport support) {
            support.register(new TestPlugin("TestRegistrar"));
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class TestPlugin implements ThreadPoolPlugin {

        private final String id;
    }

}
