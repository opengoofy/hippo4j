package cn.hippo4j.starter.handler;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.toolkit.CalculateUtil;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract threadPool runtime info.
 *
 * @author chen.ma
 * @date 2021/12/7 19:44
 */
public abstract class AbstractThreadPoolRuntime {

    /**
     * Simple info.
     *
     * @param executor
     * @return
     */
    protected PoolBaseInfo simpleInfo(Executor executor) {
        return null;
    }

    /**
     * Supplement.
     *
     * @param basePoolRunStateInfo
     * @return
     */
    protected abstract PoolRunStateInfo supplement(PoolRunStateInfo basePoolRunStateInfo);

    /**
     * Get pool run state.
     *
     * @param threadPoolId
     * @return
     */
    public PoolRunStateInfo getPoolRunState(String threadPoolId) {
        DynamicThreadPoolWrapper executorService = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        ThreadPoolExecutor pool = executorService.getExecutor();
        return getPoolRunState(threadPoolId, pool);
    }

    /**
     * Get pool run state.
     *
     * @param threadPoolId
     * @param executor
     * @return
     */
    public PoolRunStateInfo getPoolRunState(String threadPoolId, Executor executor) {
        PoolRunStateInfo stateInfo = new PoolRunStateInfo();
        if (executor != null && executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
            // 核心线程数
            int corePoolSize = pool.getCorePoolSize();
            // 最大线程数
            int maximumPoolSize = pool.getMaximumPoolSize();
            // 线程池当前线程数 (有锁)
            int poolSize = pool.getPoolSize();
            // 活跃线程数 (有锁)
            int activeCount = pool.getActiveCount();
            // 同时进入池中的最大线程数 (有锁)
            int largestPoolSize = pool.getLargestPoolSize();
            // 线程池中执行任务总数量 (有锁)
            long completedTaskCount = pool.getCompletedTaskCount();
            // 当前负载
            String currentLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "";
            // 峰值负载
            String peakLoad = CalculateUtil.divide(largestPoolSize, maximumPoolSize) + "";

            BlockingQueue<Runnable> queue = pool.getQueue();
            // 队列元素个数
            int queueSize = queue.size();
            // 队列类型
            String queueType = queue.getClass().getSimpleName();
            // 队列剩余容量
            int remainingCapacity = queue.remainingCapacity();
            // 队列容量
            int queueCapacity = queueSize + remainingCapacity;

            stateInfo.setCoreSize(corePoolSize);
            stateInfo.setTpId(threadPoolId);
            stateInfo.setPoolSize(poolSize);
            stateInfo.setMaximumSize(maximumPoolSize);
            stateInfo.setActiveSize(activeCount);
            stateInfo.setCurrentLoad(currentLoad);
            stateInfo.setPeakLoad(peakLoad);
            stateInfo.setQueueType(queueType);
            stateInfo.setQueueSize(queueSize);
            stateInfo.setQueueCapacity(queueCapacity);
            stateInfo.setQueueRemainingCapacity(remainingCapacity);
            stateInfo.setLargestPoolSize(largestPoolSize);
            stateInfo.setCompletedTaskCount(completedTaskCount);

            int rejectCount = pool instanceof DynamicThreadPoolExecutor
                    ? ((DynamicThreadPoolExecutor) pool).getRejectCount()
                    : -1;
            stateInfo.setRejectCount(rejectCount);
            stateInfo.setClientLastRefreshTime(DateUtil.formatDateTime(new Date()));
            stateInfo.setTimestamp(System.currentTimeMillis());
        }

        return supplement(stateInfo);
    }

}
