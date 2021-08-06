package com.github.dynamic.threadpool.starter.handler;

import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import com.github.dynamic.threadpool.common.model.PoolRunStateInfo;
import com.github.dynamic.threadpool.starter.core.GlobalThreadPoolManage;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread Pool Run State Service.
 *
 * @author chen.ma
 * @date 2021/7/12 21:25
 */
public class ThreadPoolRunStateHandler {

    private static InetAddress addr;

    static {
        try {
            addr = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static PoolRunStateInfo getPoolRunState(String tpId) {
        DynamicThreadPoolWrap executorService = GlobalThreadPoolManage.getExecutorService(tpId);
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
        String currentLoad = divide(activeCount, maximumPoolSize);
        // 峰值负载
        String peakLoad = divide(largestPoolSize, maximumPoolSize);

        BlockingQueue<Runnable> queue = pool.getQueue();
        // 队列类型
        String queueType = queue.getClass().getSimpleName();
        // 队列元素个数
        int queueSize = queue.size();
        // 队列剩余容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列容量
        int queueCapacity = queueSize + remainingCapacity;

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
        stateInfo.setHost(addr.getHostAddress());
        stateInfo.setTpId(tpId);

        int regectCount = pool instanceof CustomThreadPoolExecutor
                ? ((CustomThreadPoolExecutor) pool).getRegectCount()
                : -1;
        stateInfo.setRegectCount(regectCount);

        return stateInfo;
    }

    private static String divide(int num1, int num2) {
        return ((int) (Double.parseDouble(num1 + "") / Double.parseDouble(num2 + "") * 100)) + "%";
    }

}
