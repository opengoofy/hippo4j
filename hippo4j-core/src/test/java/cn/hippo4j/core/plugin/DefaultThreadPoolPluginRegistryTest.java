package cn.hippo4j.core.plugin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test for {@link DefaultThreadPoolPluginRegistry}
 *
 * @author huangchengxing
 */
public class DefaultThreadPoolPluginRegistryTest {

    private DefaultThreadPoolPluginRegistry registry;

    @Before
    public void initRegistry() {
        registry = new DefaultThreadPoolPluginRegistry();
    }

    @Test
    public void testRegister() {
        TaskAwarePlugin taskAwarePlugin = new TestTaskAwarePlugin();
        registry.register(taskAwarePlugin);
        Assert.assertThrows(IllegalArgumentException.class, () -> registry.register(taskAwarePlugin));
        Assert.assertTrue(registry.isRegistered(taskAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getTaskAwareList().size());
        Assert.assertSame(taskAwarePlugin, registry.getPlugin(taskAwarePlugin.getId()));
        registry.getAndThen(taskAwarePlugin.getId(), TestTaskAwarePlugin.class, plugin -> Assert.assertSame(plugin, taskAwarePlugin));
        Assert.assertEquals(taskAwarePlugin.getId(), registry.getAndThen(taskAwarePlugin.getId(), TestTaskAwarePlugin.class, TestTaskAwarePlugin::getId, null));
        registry.unregister(taskAwarePlugin.getId());
        Assert.assertNull(registry.getPlugin(taskAwarePlugin.getId()));

        ExecuteAwarePlugin executeAwarePlugin = new TestExecuteAwarePlugin();
        registry.register(executeAwarePlugin);
        Assert.assertTrue(registry.isRegistered(executeAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getExecuteAwareList().size());
        Assert.assertSame(executeAwarePlugin, registry.getPlugin(executeAwarePlugin.getId()));
        registry.unregister(executeAwarePlugin.getId());
        Assert.assertNull(registry.getPlugin(executeAwarePlugin.getId()));

        RejectedAwarePlugin rejectedAwarePlugin = new TestRejectedAwarePlugin();
        registry.register(rejectedAwarePlugin);
        Assert.assertTrue(registry.isRegistered(rejectedAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getRejectedAwareList().size());
        Assert.assertSame(rejectedAwarePlugin, registry.getPlugin(rejectedAwarePlugin.getId()));
        registry.unregister(rejectedAwarePlugin.getId());
        Assert.assertNull(registry.getPlugin(rejectedAwarePlugin.getId()));

        ShutdownAwarePlugin shutdownAwarePlugin = new TestShutdownAwarePlugin();
        registry.register(shutdownAwarePlugin);
        Assert.assertTrue(registry.isRegistered(shutdownAwarePlugin.getId()));
        Assert.assertEquals(1, registry.getShutdownAwareList().size());
        Assert.assertSame(shutdownAwarePlugin, registry.getPlugin(shutdownAwarePlugin.getId()));
        registry.unregister(shutdownAwarePlugin.getId());
        Assert.assertNull(registry.getPlugin(shutdownAwarePlugin.getId()));
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
