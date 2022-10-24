package cn.hippo4j.common.toolkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * memory util<br>
 * the obtained information is not invalid, after a long wait, obtain it again
 *
 * @author liuwenhao
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryUtil {

    static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    static MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
    static MemoryUsage noHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();


    /**
     * get used memory in heap
     *
     * @return long bytes
     */
    public static long heapMemoryUsed() {
        return heapMemoryUsage.getUsed();
    }

    /**
     * get max memory in heap
     *
     * @return long bytes
     */
    public static long heapMemoryMax() {
        return heapMemoryUsage.getMax();
    }


    /**
     * get free memory in heap
     *
     * @return long bytes
     */
    public static long heapMemoryFree() {
        return Math.subtractExact(heapMemoryMax(), heapMemoryUsed());
    }

    /**
     * get used memory in no-heap
     *
     * @return long bytes
     */
    public static long noHeapMemoryUsed() {
        return noHeapMemoryUsage.getUsed();
    }

    /**
     * get max memory in no-heap
     *
     * @return long bytes
     */
    public static long noHeapMemoryMax() {
        return noHeapMemoryUsage.getMax();
    }


    /**
     * get free memory in no-heap
     *
     * @return long bytes
     */
    public static long noHeapMemoryFree() {
        return Math.subtractExact(noHeapMemoryMax(), noHeapMemoryUsed());
    }

}
