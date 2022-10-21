package cn.hippo4j.core.plugin.impl;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.DefaultThreadPoolPluginRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link TaskDecoratorPlugin}
 *
 * @author huangchengxing
 */
public class TaskDecoratorPluginTest {

    private final AtomicInteger taskExecuteCount = new AtomicInteger(0);

    @Test
    public void testExecute() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginRegistry(),
            5, 5, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy()
        );
        TaskDecoratorPlugin plugin = new TaskDecoratorPlugin();
        plugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        plugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        executor.register(plugin);
        executor.execute(() -> {});
        ThreadUtil.sleep(500L);
        Assert.assertEquals(2, taskExecuteCount.get());
    }

}
