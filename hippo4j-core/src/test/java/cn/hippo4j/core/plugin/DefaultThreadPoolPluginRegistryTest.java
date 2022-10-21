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
        Assert.assertTrue(registry.isRegistered(taskAwarePlugin));
        Assert.assertEquals(1, registry.getTaskAwareList().size());

        ExecuteAwarePlugin executeAwarePlugin = new TestExecuteAwarePlugin();
        registry.register(executeAwarePlugin);
        Assert.assertTrue(registry.isRegistered(executeAwarePlugin));
        Assert.assertEquals(1, registry.getExecuteAwareList().size());

        RejectedAwarePlugin rejectedAwarePlugin = new TestRejectedAwarePlugin();
        registry.register(rejectedAwarePlugin);
        Assert.assertTrue(registry.isRegistered(rejectedAwarePlugin));
        Assert.assertEquals(1, registry.getRejectedAwareList().size());

        ShutdownAwarePlugin shutdownAwarePlugin = new TestShutdownAwarePlugin();
        registry.register(shutdownAwarePlugin);
        Assert.assertTrue(registry.isRegistered(shutdownAwarePlugin));
        Assert.assertEquals(1, registry.getShutdownAwareList().size());
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
