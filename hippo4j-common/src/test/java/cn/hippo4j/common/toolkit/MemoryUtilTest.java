package cn.hippo4j.common.toolkit;


import org.junit.Assert;
import org.junit.Test;

public class MemoryUtilTest {

    @Test
    public void heapMemoryUsed() {
        long memoryUsed = MemoryUtil.heapMemoryUsed();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void heapMemoryMax() {
        long memoryUsed = MemoryUtil.heapMemoryMax();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void heapMemoryFree() {
        long memoryUsed = MemoryUtil.heapMemoryFree();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void noHeapMemoryUsed() {
        long memoryUsed = MemoryUtil.noHeapMemoryUsed();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void noHeapMemoryMax() {
        long memoryUsed = MemoryUtil.noHeapMemoryMax();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void noHeapMemoryFree() {
        long memoryUsed = MemoryUtil.noHeapMemoryFree();
        Assert.assertNotEquals(0, memoryUsed);
    }
}