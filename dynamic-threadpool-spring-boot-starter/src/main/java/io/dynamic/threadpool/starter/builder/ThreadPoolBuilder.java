package io.dynamic.threadpool.starter.builder;


import io.dynamic.threadpool.common.enums.QueueTypeEnum;
import io.dynamic.threadpool.common.toolkit.Assert;
import io.dynamic.threadpool.starter.toolkit.BlockingQueueUtil;
import io.dynamic.threadpool.starter.toolkit.thread.AbstractBuildThreadPoolTemplate;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * 线程池构造器
 *
 * @author chen.ma
 * @date 2021/6/28 17:29
 */
public class ThreadPoolBuilder implements Builder<ThreadPoolExecutor> {

    /**
     * 是否创建快速消费线程池
     */
    private boolean isFastPool;

    /**
     * 核心线程数量
     */
    private Integer corePoolNum = calculateCoreNum();

    /**
     * 最大线程数量
     */
    private Integer maxPoolNum = corePoolNum + (corePoolNum >> 1);

    /**
     * 线程存活时间
     */
    private Long keepAliveTime = 30000L;

    /**
     * 线程存活时间单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 队列最大容量
     */
    private Integer capacity = 512;

    /**
     * 队列类型枚举
     */
    private QueueTypeEnum queueType;

    /**
     * 阻塞队列
     */
    private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

    /**
     * 线程池任务满时拒绝任务策略
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * 是否守护线程
     */
    private boolean isDaemon = false;

    /**
     * 线程名称前缀
     */
    private String threadNamePrefix;

    /**
     * 计算公式：CPU 核数 / (1 - 阻塞系数 0.8)
     *
     * @return 线程池核心线程数
     */
    private Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

    public ThreadPoolBuilder isFastPool(Boolean isFastPool) {
        this.isFastPool = isFastPool;
        return this;
    }

    public ThreadPoolBuilder threadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }

    public ThreadPoolBuilder threadFactory(String threadNamePrefix, Boolean isDaemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
        return this;
    }

    public ThreadPoolBuilder corePoolNum(Integer corePoolNum) {
        this.corePoolNum = corePoolNum;
        return this;
    }

    public ThreadPoolBuilder maxPoolNum(Integer maxPoolNum) {
        this.maxPoolNum = maxPoolNum;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ThreadPoolBuilder capacity(Integer capacity) {
        this.capacity = capacity;
        return this;
    }

    public ThreadPoolBuilder rejected(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    /**
     * 使用此方式赋值 workQueue, capacity 失效
     *
     * @param queueType
     * @return
     */
    public ThreadPoolBuilder workQueue(QueueTypeEnum queueType) {
        this.queueType = queueType;
        return this;
    }

    /**
     * 构建
     *
     * @return
     */
    @Override
    public ThreadPoolExecutor build() {
        return isFastPool ? buildFastPool(this) : buildPool(this);
    }

    /**
     * 创建
     *
     * @return
     */
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    /**
     * 构建普通线程池
     *
     * @param builder
     * @return
     */
    public static ThreadPoolExecutor buildPool(ThreadPoolBuilder builder) {
        return AbstractBuildThreadPoolTemplate.buildPool(buildInitParam(builder));
    }

    /**
     * 构建快速消费线程池
     *
     * @param builder
     * @return
     */
    public static ThreadPoolExecutor buildFastPool(ThreadPoolBuilder builder) {
        return AbstractBuildThreadPoolTemplate.buildFastPool(buildInitParam(builder));
    }

    /**
     * 构建初始化参数
     *
     * @param builder
     * @return
     */
    private static AbstractBuildThreadPoolTemplate.ThreadPoolInitParam buildInitParam(ThreadPoolBuilder builder) {
        Assert.notEmpty(builder.threadNamePrefix, "线程名称前缀不可为空或空的字符串.");
        AbstractBuildThreadPoolTemplate.ThreadPoolInitParam initParam =
                new AbstractBuildThreadPoolTemplate.ThreadPoolInitParam(builder.threadNamePrefix, builder.isDaemon);

        initParam.setCorePoolNum(builder.corePoolNum)
                .setMaxPoolNum(builder.maxPoolNum)
                .setKeepAliveTime(builder.keepAliveTime)
                .setCapacity(builder.capacity)
                .setRejectedExecutionHandler(builder.rejectedExecutionHandler)
                .setTimeUnit(builder.timeUnit);

        // 快速消费线程池内置指定线程池
        if (!builder.isFastPool) {
            BlockingQueue blockingQueue = BlockingQueueUtil.createBlockingQueue(builder.queueType.type, builder.capacity);
            if (blockingQueue == null) {
                blockingQueue = builder.workQueue;
            }
            initParam.setWorkQueue(blockingQueue);
        }

        return initParam;
    }
}
