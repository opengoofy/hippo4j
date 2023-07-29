package cn.hippo4j.common.executor.support;

import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Shizi
 * @version 1.0
 * Create by 2023-07-29 18:43
 */
@Slf4j
public class ThreadPoolExecutorUtilTest {

    private ThreadPoolExecutor executor;
    private int corePoolSize;
    private int maxPoolSize;

    @Before
    public void testSafeSetPoolSize() {
        corePoolSize = 2;
        maxPoolSize = 4;
        executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                1L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
    }

    @Test
    public void testEquals(){
        // Test when the new core pool size equals the original maximum pool size.
        int newCorePoolSize1 = maxPoolSize;
        int newMaxPoolSize1 = 6;
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, newCorePoolSize1, newMaxPoolSize1);
        Assert.assertEquals(newCorePoolSize1, executor.getCorePoolSize());
        Assert.assertEquals(newMaxPoolSize1, executor.getMaximumPoolSize());
    }

    @Test
    public void testGreater(){
        // Test when the new core pool size is greater than the original maximum pool size.
        int newCorePoolSize2 = 8;
        int newMaxPoolSize2 = 10;
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, newCorePoolSize2, newMaxPoolSize2);
        Assert.assertEquals(newCorePoolSize2, executor.getCorePoolSize());
        Assert.assertEquals(newMaxPoolSize2, executor.getMaximumPoolSize());
    }

    @Test
    public void testLess(){
        // Test when the new core pool size is less than the original maximum pool size.
        int newCorePoolSize3 = 3;
        int newMaxPoolSize3 = 5;
        ThreadPoolExecutorUtil.safeSetPoolSize(executor, newCorePoolSize3, newMaxPoolSize3);
        Assert.assertEquals(newCorePoolSize3, executor.getCorePoolSize());
        Assert.assertEquals(newMaxPoolSize3, executor.getMaximumPoolSize());
    }

    @Test
    public void testException(){
        // Test when the new core pool size is greater than the new maximum pool size, which should throw an IllegalArgumentException.
        int newCorePoolSize4 = 6;
        int newMaxPoolSize4 = 4;
        try {
            ThreadPoolExecutorUtil.safeSetPoolSize(executor, newCorePoolSize4, newMaxPoolSize4);
        } catch (IllegalArgumentException e) {
            // Expected to throw an exception.
            Assert.assertEquals("newCorePoolSize must be smaller than newMaximumPoolSize", e.getMessage());
            log.error("newCorePoolSize must be smaller than newMaximumPoolSize",e.getMessage());
        }
    }
}

