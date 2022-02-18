package cn.hippo4j.starter.toolkit.thread;

import cn.hippo4j.common.design.builder.Builder;
import cn.hippo4j.common.toolkit.Assert;
import org.springframework.core.task.TaskDecorator;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * ThreadPool builder.
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
     * 是否动态线程池
     */
    private boolean isDynamicPool;

    /**
     * 核心线程数量
     */
    private int corePoolSize = calculateCoreNum();

    /**
     * 最大线程数量
     */
    private int maxPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * 线程存活时间
     */
    private long keepAliveTime = 30000L;

    /**
     * 线程存活时间单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 队列最大容量
     */
    private int capacity = 512;

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
     * 线程池 ID
     */
    private String threadPoolId;

    /**
     * 是否告警
     */
    private boolean isAlarm = false;

    /**
     * 线程任务装饰器
     */
    private TaskDecorator taskDecorator;

    /**
     * 等待终止毫秒
     */
    private Long awaitTerminationMillis = 5000L;

    /**
     * 等待任务在关机时完成
     */
    private Boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 允许核心线程超时
     */
    private Boolean allowCoreThreadTimeOut = false;

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

    public ThreadPoolBuilder dynamicPool() {
        this.isDynamicPool = true;
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

    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolBuilder maxPoolNum(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public ThreadPoolBuilder poolThreadSize(int corePoolSize, int maxPoolSize) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        return this;
    }

    public ThreadPoolBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public ThreadPoolBuilder workQueue(QueueTypeEnum queueType, int capacity) {
        this.queueType = queueType;
        this.capacity = capacity;
        return this;
    }

    public ThreadPoolBuilder rejected(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    public ThreadPoolBuilder workQueue(QueueTypeEnum queueType) {
        this.queueType = queueType;
        return this;
    }

    public ThreadPoolBuilder workQueue(BlockingQueue workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ThreadPoolBuilder threadPoolId(String threadPoolId) {
        this.threadPoolId = threadPoolId;
        return this;
    }

    public ThreadPoolBuilder taskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
        return this;
    }

    public ThreadPoolBuilder awaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        return this;
    }

    public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }

    public ThreadPoolBuilder dynamicSupport(boolean waitForTasksToCompleteOnShutdown, long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }

    public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * 构建.
     *
     * @return
     */
    @Override
    public ThreadPoolExecutor build() {
        if (isDynamicPool) {
            return buildDynamicPool(this);
        }
        return isFastPool ? buildFastPool(this) : buildPool(this);
    }

    /**
     * 创建.
     *
     * @return
     */
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    /**
     * 构建普通线程池.
     *
     * @param builder
     * @return
     */
    private static ThreadPoolExecutor buildPool(ThreadPoolBuilder builder) {
        return AbstractBuildThreadPoolTemplate.buildPool(buildInitParam(builder));
    }

    /**
     * 构建快速消费线程池.
     *
     * @param builder
     * @return
     */
    private static ThreadPoolExecutor buildFastPool(ThreadPoolBuilder builder) {
        return AbstractBuildThreadPoolTemplate.buildFastPool(buildInitParam(builder));
    }

    /**
     * 构建动态线程池.
     *
     * @param builder
     * @return
     */
    private static ThreadPoolExecutor buildDynamicPool(ThreadPoolBuilder builder) {
        return AbstractBuildThreadPoolTemplate.buildDynamicPool(buildInitParam(builder));
    }

    /**
     * 构建初始化参数.
     *
     * @param builder
     * @return
     */
    private static AbstractBuildThreadPoolTemplate.ThreadPoolInitParam buildInitParam(ThreadPoolBuilder builder) {
        Assert.notEmpty(builder.threadNamePrefix, "The thread name prefix cannot be empty or an empty string.");
        AbstractBuildThreadPoolTemplate.ThreadPoolInitParam initParam =
                new AbstractBuildThreadPoolTemplate.ThreadPoolInitParam(builder.threadNamePrefix, builder.isDaemon);

        initParam.setCorePoolNum(builder.corePoolSize)
                .setMaxPoolNum(builder.maxPoolSize)
                .setKeepAliveTime(builder.keepAliveTime)
                .setCapacity(builder.capacity)
                .setRejectedExecutionHandler(builder.rejectedExecutionHandler)
                .setTimeUnit(builder.timeUnit)
                .setAllowCoreThreadTimeOut(builder.allowCoreThreadTimeOut)
                .setTaskDecorator(builder.taskDecorator);

        if (builder.isDynamicPool) {
            String threadPoolId = Optional.ofNullable(builder.threadPoolId).orElse(builder.threadNamePrefix);
            initParam.setThreadPoolId(threadPoolId);
            initParam.setWaitForTasksToCompleteOnShutdown(builder.waitForTasksToCompleteOnShutdown);
            initParam.setAwaitTerminationMillis(builder.awaitTerminationMillis);
        }

        if (!builder.isFastPool) {
            if (builder.queueType != null) {
                builder.workQueue = QueueTypeEnum.createBlockingQueue(builder.queueType.type, builder.capacity);
            }
            initParam.setWorkQueue(builder.workQueue);
        }

        return initParam;
    }

}
