package cn.hippo4j.common.executor.support;

import cn.hippo4j.common.toolkit.ThreadUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Synchronous placement queue policy implementation test
 */
public class SyncPutQueuePolicyTest {

    /**
     * test thread pool rejected execution
     */
    @Test
    public void testRejectedExecution() throws InterruptedException {
        SyncPutQueuePolicy syncPutQueuePolicy = new SyncPutQueuePolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), syncPutQueuePolicy);
        threadPoolExecutor.prestartAllCoreThreads();

        Assert.assertSame(syncPutQueuePolicy, threadPoolExecutor.getRejectedExecutionHandler());
        IntStream.range(0, 4).forEach(s -> {
            threadPoolExecutor.execute(() -> ThreadUtil.sleep(500L));
        });
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
        }
        Assert.assertEquals(4, threadPoolExecutor.getCompletedTaskCount());
    }
}
