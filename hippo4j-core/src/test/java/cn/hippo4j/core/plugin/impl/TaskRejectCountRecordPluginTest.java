package cn.hippo4j.core.plugin.impl;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.DefaultThreadPoolPluginManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link TaskRejectCountRecordPlugin}
 *
 * @author huangchengxing
 */
public class TaskRejectCountRecordPluginTest {

    @Test
    public void testExecute() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            1, 1, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy()
        );

        TaskRejectCountRecordPlugin plugin = new TaskRejectCountRecordPlugin();
        executor.register(plugin);
        executor.submit(() -> ThreadUtil.sleep(500L));
        executor.submit(() -> ThreadUtil.sleep(500L));
        executor.submit(() -> ThreadUtil.sleep(500L));

        ThreadUtil.sleep(500L);
        Assert.assertEquals((Long)1L, plugin.getRejectCountNum());
    }

}
