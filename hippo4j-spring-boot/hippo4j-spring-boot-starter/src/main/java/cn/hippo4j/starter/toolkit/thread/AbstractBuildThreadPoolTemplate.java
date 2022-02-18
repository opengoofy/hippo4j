package cn.hippo4j.starter.toolkit.thread;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Abstract build threadPool template.
 *
 * @author chen.ma
 * @date 2021/7/5 21:45
 */
@Slf4j
public class AbstractBuildThreadPoolTemplate {

    /**
     * 线程池构建初始化参数.
     * <p>
     * 此处本身是模版设计方法, 但是考虑创建简洁性, 移除 abstract.
     * 异常参考 {@link AbstractQueuedSynchronizer#tryAcquire}
     *
     * @return
     */
    protected static ThreadPoolInitParam initParam() {
        throw new UnsupportedOperationException();
    }

    /**
     * 构建线程池.
     *
     * @return
     */
    public static ThreadPoolExecutor buildPool() {
        ThreadPoolInitParam initParam = initParam();
        return buildPool(initParam);
    }

    /**
     * 构建线程池.
     *
     * @return
     */
    public static ThreadPoolExecutor buildPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        ThreadPoolExecutor executorService;
        try {
            executorService = new ThreadPoolExecutorTemplate(initParam.getCorePoolNum(),
                    initParam.getMaxPoolNum(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    initParam.getWorkQueue(),
                    initParam.getThreadFactory(),
                    initParam.rejectedExecutionHandler);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Error creating thread pool parameter.", ex);
        }

        executorService.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return executorService;
    }

    /**
     * 构建快速执行线程池.
     *
     * @return
     */
    public static ThreadPoolExecutor buildFastPool() {
        ThreadPoolInitParam initParam = initParam();
        return buildFastPool(initParam);
    }

    /**
     * 构建快速执行线程池.
     *
     * @return
     */
    public static ThreadPoolExecutor buildFastPool(ThreadPoolInitParam initParam) {
        TaskQueue<Runnable> taskQueue = new TaskQueue(initParam.getCapacity());
        FastThreadPoolExecutor fastThreadPoolExecutor;
        try {
            fastThreadPoolExecutor = new FastThreadPoolExecutor(initParam.getCorePoolNum(),
                    initParam.getMaxPoolNum(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    taskQueue,
                    initParam.getThreadFactory(),
                    initParam.rejectedExecutionHandler);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Error creating thread pool parameter.", ex);
        }

        taskQueue.setExecutor(fastThreadPoolExecutor);
        fastThreadPoolExecutor.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return fastThreadPoolExecutor;
    }

    /**
     * 构建动态线程池.
     *
     * @param initParam
     * @return
     */
    public static DynamicThreadPoolExecutor buildDynamicPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
        try {
            dynamicThreadPoolExecutor = new DynamicThreadPoolExecutor(
                    initParam.getCorePoolNum(),
                    initParam.getMaxPoolNum(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    initParam.getWaitForTasksToCompleteOnShutdown(),
                    initParam.getAwaitTerminationMillis(),
                    initParam.getWorkQueue(),
                    initParam.getThreadPoolId(),
                    initParam.getThreadFactory(),
                    initParam.getRejectedExecutionHandler()
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(String.format("Error creating thread pool parameter. threadPool id :: %s", initParam.getThreadPoolId()), ex);
        }

        dynamicThreadPoolExecutor.setTaskDecorator(initParam.getTaskDecorator());
        dynamicThreadPoolExecutor.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return dynamicThreadPoolExecutor;
    }

    @Data
    @Accessors(chain = true)
    public static class ThreadPoolInitParam {

        /**
         * 核心线程数量
         */
        private Integer corePoolNum;

        /**
         * 最大线程数量
         */
        private Integer maxPoolNum;

        /**
         * 线程存活时间
         */
        private Long keepAliveTime;

        /**
         * 线程存活时间单位
         */
        private TimeUnit timeUnit;

        /**
         * 队列最大容量
         */
        private Integer capacity;

        /**
         * 阻塞队列
         */
        private BlockingQueue<Runnable> workQueue;

        /**
         * 线程池任务满时拒绝任务策略
         */
        private RejectedExecutionHandler rejectedExecutionHandler;

        /**
         * 创建线程工厂
         */
        private ThreadFactory threadFactory;

        /**
         * 线程 ID
         */
        private String threadPoolId;

        /**
         * 线程任务装饰器
         */
        private TaskDecorator taskDecorator;

        /**
         * 等待终止毫秒
         */
        private Long awaitTerminationMillis;

        /**
         * 等待任务在关机时完成
         */
        private Boolean waitForTasksToCompleteOnShutdown;

        /**
         * 允许核心线程超时
         */
        private Boolean allowCoreThreadTimeOut = false;

        public ThreadPoolInitParam(String threadNamePrefix, boolean isDaemon) {
            this.threadPoolId = threadNamePrefix;
            this.threadFactory = ThreadFactoryBuilder.builder()
                    .prefix(threadNamePrefix)
                    .daemon(isDaemon)
                    .build();
        }
    }

}
