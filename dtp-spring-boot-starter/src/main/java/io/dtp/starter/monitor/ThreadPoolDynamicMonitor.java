package io.dtp.starter.monitor;

import io.dtp.starter.core.GlobalThreadPoolManage;
import io.dtp.starter.core.ResizableCapacityLinkedBlockIngQueue;
import io.dtp.starter.wrap.DynamicThreadPoolWrap;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池动态监听
 *
 * @author chen.ma
 * @date 2021/6/20 15:51
 */
public class ThreadPoolDynamicMonitor {

    public void dynamicPool(String threadPoolName, Integer coreSize, Integer maxSize, Integer capacity, Long keepAliveTime) {
        DynamicThreadPoolWrap wrap = GlobalThreadPoolManage.getExecutorService(threadPoolName);
        ThreadPoolExecutor executor = wrap.getPool();
        if (coreSize != null) {
            executor.setCorePoolSize(coreSize);
        }
        if (maxSize != null) {
            executor.setMaximumPoolSize(maxSize);
        }
        if (capacity != null) {
            ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) executor.getQueue();
            queue.setCapacity(capacity);
        }
        if (keepAliveTime != null) {
            executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
        }
    }
}
