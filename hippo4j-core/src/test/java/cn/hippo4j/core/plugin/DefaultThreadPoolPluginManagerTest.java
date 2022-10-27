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

import cn.hippo4j.core.plugin.manager.DefaultThreadPoolPluginManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test for {@link DefaultThreadPoolPluginManager}
 */
public class DefaultThreadPoolPluginManagerTest {

    private DefaultThreadPoolPluginManager registry;

    @Before
    public void initRegistry() {
        registry = new DefaultThreadPoolPluginManager();
    }

    @Test
    public void testRegister() {
        TaskAwarePlugin taskAwarePlugin = new TestTaskAwarePlugin();
        registry.register(taskAwarePlugin);
        Assert.assertThrows(IllegalArgumentException.class, () -> registry.register(taskAwarePlugin));
        Assert.assertTrue(registry.isRegistered(taskAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getTaskAwarePluginList().size());
        Assert.assertSame(taskAwarePlugin, registry.getPlugin(taskAwarePlugin.getId()).orElse(null));
        registry.getPluginOfType(taskAwarePlugin.getId(), TestTaskAwarePlugin.class)
                .ifPresent(plugin -> Assert.assertSame(plugin, taskAwarePlugin));
        Assert.assertEquals(taskAwarePlugin.getId(), registry.getPluginOfType(taskAwarePlugin.getId(), TestTaskAwarePlugin.class).map(TestTaskAwarePlugin::getId).orElse(null));
        registry.unregister(taskAwarePlugin.getId());
        Assert.assertFalse(registry.getPlugin(taskAwarePlugin.getId()).isPresent());

        ExecuteAwarePlugin executeAwarePlugin = new TestExecuteAwarePlugin();
        registry.register(executeAwarePlugin);
        Assert.assertTrue(registry.isRegistered(executeAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getExecuteAwarePluginList().size());

        RejectedAwarePlugin rejectedAwarePlugin = new TestRejectedAwarePlugin();
        registry.register(rejectedAwarePlugin);
        Assert.assertTrue(registry.isRegistered(rejectedAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getRejectedAwarePluginList().size());

        ShutdownAwarePlugin shutdownAwarePlugin = new TestShutdownAwarePlugin();
        registry.register(shutdownAwarePlugin);
        Assert.assertTrue(registry.isRegistered(shutdownAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getShutdownAwarePluginList().size());
    }

    private final static class TestTaskAwarePlugin implements TaskAwarePlugin {

        /**
         * Get id.
         *
         * @return id
         */
        @Override
        public String getId() {
            return "TestTaskAwarePlugin";
        }
    }

    private final static class TestExecuteAwarePlugin implements ExecuteAwarePlugin {

        /**
         * Get id.
         *
         * @return id
         */
        @Override
        public String getId() {
            return "TestExecuteAwarePlugin";
        }
    }

    private final static class TestRejectedAwarePlugin implements RejectedAwarePlugin {

        /**
         * Get id.
         *
         * @return id
         */
        @Override
        public String getId() {
            return "TestRejectedAwarePlugin";
        }
    }

    private final static class TestShutdownAwarePlugin implements ShutdownAwarePlugin {

        /**
         * Get id.
         *
         * @return id
         */
        @Override
        public String getId() {
            return "TestShutdownAwarePlugin";
        }
    }

}
