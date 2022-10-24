package cn.hippo4j.core.plugin.impl;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.DefaultThreadPoolPluginRegistry;
import cn.hippo4j.core.plugin.ThreadPoolPlugin;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link ThreadPoolExecutorShutdownPlugin}
 *
 * @author huangchengxing
 */
public class ThreadPoolExecutorShutdownPluginTest {

    public ExtensibleThreadPoolExecutor getExecutor(ThreadPoolPlugin plugin) {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginRegistry(),
            2, 2, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy()
        );
        executor.register(plugin);
        return executor;
    }

    private static Callable<Integer> getCallable(AtomicInteger completedCount) {
        return () -> {
            ThreadUtil.sleep(1000L);
            return completedCount.incrementAndGet();
        };
    }

    @Test
    public void testExecuteShutdownWhenWaitTaskCompleted() {
        ExtensibleThreadPoolExecutor executor = getExecutor(
            new ThreadPoolExecutorShutdownPlugin(2000L, true)
        );

        AtomicInteger completedCount = new AtomicInteger(0);
        Callable<Integer> future1 = getCallable(completedCount);
        Callable<Integer> future2 = getCallable(completedCount);
        executor.submit(future1);
        executor.submit(future2);

        executor.shutdown();
        Assert.assertEquals(2, completedCount.get());
    }

    @Test
    public void testExecuteShutdownWhenNotWaitTaskCompleted() {
        ExtensibleThreadPoolExecutor executor = getExecutor(
            new ThreadPoolExecutorShutdownPlugin(-1L, true)
        );

        AtomicInteger completedCount = new AtomicInteger(0);
        Callable<Integer> future1 = getCallable(completedCount);
        Callable<Integer> future2 = getCallable(completedCount);
        executor.submit(future1);
        executor.submit(future2);

        executor.shutdown();
        Assert.assertEquals(0, completedCount.get());
    }
}