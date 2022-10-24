package cn.hippo4j.core.plugin.impl;

import cn.hippo4j.common.toolkit.SyncTimeRecorder;
import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.DefaultThreadPoolPluginRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link TaskTimeRecordPlugin}
 *
 * @author huangchengxing
 */
public class TaskTimeRecordPluginTest {

    @Test
    public void testExecute() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginRegistry(),
            3, 3, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy()
        );

        TaskTimeRecordPlugin plugin = new TaskTimeRecordPlugin();
        executor.register(plugin);
        executor.submit(() -> ThreadUtil.sleep(100L));
        executor.submit(() -> ThreadUtil.sleep(300L));
        executor.submit(() -> ThreadUtil.sleep(200L));

        ThreadUtil.sleep(1000L);
        SyncTimeRecorder.Summary summary = plugin.summarize();
        Assert.assertEquals(1, summary.getMinTaskTime() / 100L);
        Assert.assertEquals(3, summary.getMaxTaskTime() / 100L);
        Assert.assertEquals(2, summary.getAvgTaskTimeMillis() / 100L);
        Assert.assertEquals(6, summary.getTotalTaskTime() / 100L);
    }
}
