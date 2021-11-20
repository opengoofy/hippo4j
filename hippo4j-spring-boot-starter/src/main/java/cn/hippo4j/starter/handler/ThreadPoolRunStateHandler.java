package cn.hippo4j.starter.handler;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.toolkit.ByteConvertUtil;
import cn.hippo4j.starter.toolkit.CalculateUtil;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pool run state service.
 *
 * @author chen.ma
 * @date 2021/7/12 21:25
 */
@Slf4j
public class ThreadPoolRunStateHandler {

    private static InetAddress INET_ADDRESS;

    static {
        try {
            INET_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            log.error("Local IP acquisition failed.", ex);
        }
    }

    public static PoolRunStateInfo getPoolRunState(String tpId) {
        DynamicThreadPoolWrapper executorService = GlobalThreadPoolManage.getExecutorService(tpId);
        ThreadPoolExecutor pool = executorService.getPool();

        // 核心线程数
        int corePoolSize = pool.getCorePoolSize();
        // 最大线程数
        int maximumPoolSize = pool.getMaximumPoolSize();
        // 线程池当前线程数
        int poolSize = pool.getPoolSize();
        // 活跃线程数
        int activeCount = pool.getActiveCount();
        // 同时进入池中的最大线程数
        int largestPoolSize = pool.getLargestPoolSize();
        // 线程池中执行任务总数量
        long completedTaskCount = pool.getCompletedTaskCount();
        // 当前负载
        String currentLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "%";
        // 峰值负载
        String peakLoad = CalculateUtil.divide(largestPoolSize, maximumPoolSize) + "%";

        BlockingQueue<Runnable> queue = pool.getQueue();
        // 队列类型
        String queueType = queue.getClass().getSimpleName();
        // 队列元素个数
        int queueSize = queue.size();
        // 队列剩余容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列容量
        int queueCapacity = queueSize + remainingCapacity;

        // 内存占比: 使用内存 / 最大内存
        RuntimeInfo runtimeInfo = new RuntimeInfo();
        String memoryProportion = StrUtil.builder(
                "已分配: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getTotalMemory()),
                " / 最大可用: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getMaxMemory())
        ).toString();

        PoolRunStateInfo stateInfo = new PoolRunStateInfo();
        stateInfo.setCoreSize(corePoolSize);
        stateInfo.setMaximumSize(maximumPoolSize);
        stateInfo.setPoolSize(poolSize);
        stateInfo.setActiveSize(activeCount);
        stateInfo.setCurrentLoad(currentLoad);
        stateInfo.setPeakLoad(peakLoad);
        stateInfo.setQueueType(queueType);
        stateInfo.setQueueSize(queueSize);
        stateInfo.setQueueRemainingCapacity(remainingCapacity);
        stateInfo.setQueueCapacity(queueCapacity);
        stateInfo.setLargestPoolSize(largestPoolSize);
        stateInfo.setCompletedTaskCount(completedTaskCount);
        stateInfo.setHost(INET_ADDRESS.getHostAddress());
        stateInfo.setTpId(tpId);
        stateInfo.setMemoryProportion(memoryProportion);
        stateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(runtimeInfo.getFreeMemory()));

        int rejectCount = pool instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) pool).getRejectCount()
                : -1;
        stateInfo.setRejectCount(rejectCount);

        return stateInfo;
    }

}
