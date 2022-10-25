package cn.hippo4j.core.executor;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.plugin.*;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link ExtensibleThreadPoolExecutor}
 *
 * @author huangchengxing
 */
public class ExtensibleThreadPoolExecutorTest {

    private final RejectedExecutionHandler originalHandler = new ThreadPoolExecutor.DiscardPolicy();

    private ExtensibleThreadPoolExecutor executor;

    @Before
    public void initExecutor() {
        executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            5, 5, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, originalHandler
        );
    }

    @Test
    public void testGetOrSetRejectedHandler() {
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        executor.setRejectedExecutionHandler(handler);
        Assert.assertSame(handler, executor.getRejectedExecutionHandler());
    }

    @Test
    public void testInvokeTaskAwarePlugin() {
        TestTaskAwarePlugin plugin = new TestTaskAwarePlugin();
        executor.register(plugin);
        executor.submit(() -> {});
        executor.submit(() -> true);
        executor.submit(() -> {}, false);
        Assert.assertEquals(3, plugin.getInvokeCount().get());
    }

    @Test
    public void testInvokeExecuteAwarePlugin() {
        TestExecuteAwarePlugin plugin = new TestExecuteAwarePlugin();
        executor.register(plugin);
        executor.execute(() -> {});
        ThreadUtil.sleep(500L);
        Assert.assertEquals(3, plugin.getInvokeCount().get());
    }

    @Test
    public void testInvokeRejectedAwarePlugin() {
        executor.setCorePoolSize(1);
        executor.setMaximumPoolSize(1);

        TestRejectedAwarePlugin plugin = new TestRejectedAwarePlugin();
        executor.register(plugin);
        // blocking pool and queue
        executor.submit(() -> ThreadUtil.sleep(500L));
        executor.submit(() -> ThreadUtil.sleep(500L));
        // reject 3 tasks
        executor.submit(() -> {});
        executor.submit(() -> {});
        executor.submit(() -> {});

        ThreadUtil.sleep(500L);
        Assert.assertEquals(3, plugin.getInvokeCount().get());
    }

    @Test
    public void testInvokeTestShutdownAwarePluginWhenShutdown() throws InterruptedException {
        TestShutdownAwarePlugin plugin = new TestShutdownAwarePlugin();
        executor.register(plugin);
        executor.shutdown();
        if (executor.awaitTermination(500L, TimeUnit.MILLISECONDS)) {
            Assert.assertEquals(3, plugin.getInvokeCount().get());
        }
    }

    @Test
    public void testInvokeTestShutdownAwarePluginWhenShutdownNow() throws InterruptedException {
        TestShutdownAwarePlugin plugin = new TestShutdownAwarePlugin();
        executor.register(plugin);
        executor.shutdownNow();
        if (executor.awaitTermination(500L, TimeUnit.MILLISECONDS)) {
            Assert.assertEquals(3, plugin.getInvokeCount().get());
        }
    }

    @Getter
    private final static class TestTaskAwarePlugin implements TaskAwarePlugin {
        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestTaskAwarePlugin";
        @Override
        public <V> Runnable beforeTaskCreate(ThreadPoolExecutor executor, Runnable runnable, V value) {
            invokeCount.incrementAndGet();
            return TaskAwarePlugin.super.beforeTaskCreate(executor, runnable, value);
        }
        @Override
        public <V> Callable<V> beforeTaskCreate(ThreadPoolExecutor executor, Callable<V> future) {
            invokeCount.incrementAndGet();
            return TaskAwarePlugin.super.beforeTaskCreate(executor, future);
        }
    }

    @Getter
    private final static class TestExecuteAwarePlugin implements ExecuteAwarePlugin {
        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestExecuteAwarePlugin";
        @Override
        public void beforeExecute(Thread thread, Runnable runnable) {
            invokeCount.incrementAndGet();
            ExecuteAwarePlugin.super.beforeExecute(thread, runnable);
        }
        @Override
        public Runnable execute(Runnable runnable) {
            invokeCount.incrementAndGet();
            return ExecuteAwarePlugin.super.execute(runnable);
        }
        @Override
        public void afterExecute(Runnable runnable, Throwable throwable) {
            invokeCount.incrementAndGet();
            ExecuteAwarePlugin.super.afterExecute(runnable, throwable);
        }
    }

    @Getter
    private final static class TestRejectedAwarePlugin implements RejectedAwarePlugin {
        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestRejectedAwarePlugin";
        @Override
        public void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            invokeCount.incrementAndGet();
        }
    }

    @Getter
    private final static class TestShutdownAwarePlugin implements ShutdownAwarePlugin {
        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestShutdownAwarePlugin";
        @Override
        public void beforeShutdown(ThreadPoolExecutor executor) {
            invokeCount.incrementAndGet();
            ShutdownAwarePlugin.super.beforeShutdown(executor);
        }
        @Override
        public void afterShutdown(ThreadPoolExecutor executor, List<Runnable> remainingTasks) {
            invokeCount.incrementAndGet();
            ShutdownAwarePlugin.super.afterShutdown(executor, remainingTasks);
        }
        @Override
        public void afterTerminated(ExtensibleThreadPoolExecutor executor) {
            invokeCount.incrementAndGet();
            ShutdownAwarePlugin.super.afterTerminated(executor);
        }
    }

}
