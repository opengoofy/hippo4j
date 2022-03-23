package cn.hippo4j.common.notify.request;

import cn.hippo4j.common.notify.NotifyTypeEnum;
import cn.hippo4j.common.notify.request.base.BaseNotifyRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Alarm notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 19:41
 */
@Data
@Accessors(chain = true)
public class AlarmNotifyRequest extends BaseNotifyRequest {

    /**
     * interval
     */
    private Integer interval;

    /**
     * notifyTypeEnum
     */
    private NotifyTypeEnum notifyTypeEnum;

    /**
     * active
     */
    private String active;

    /**
     * appName
     */
    private String appName;

    /**
     * identify
     */
    private String identify;

    /**
     * corePoolSize
     */
    private Integer corePoolSize;

    /**
     * maximumPoolSize
     */
    private Integer maximumPoolSize;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * activeCount
     */
    private Integer activeCount;

    /**
     * largestPoolSize
     */
    private Integer largestPoolSize;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * queueName
     */
    private String queueName;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * remainingCapacity
     */
    private Integer remainingCapacity;

    /**
     * rejectedExecutionHandlerName
     */
    private String rejectedExecutionHandlerName;

    /**
     * rejectCountNum
     */
    private Long rejectCountNum;

    /**
     * executeTime
     */
    private Long executeTime;

    /**
     * executeTimeOut
     */
    private Long executeTimeOut;

    /**
     * executeTimeoutTrace
     */
    private String executeTimeoutTrace;

    public void initThreadPoolExecutorProperty(ThreadPoolExecutor threadPoolExecutor) {
        // 核心线程数
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        // 最大线程数
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        // 线程池当前线程数 (有锁)
        int poolSize = threadPoolExecutor.getPoolSize();
        // 活跃线程数 (有锁)
        int activeCount = threadPoolExecutor.getActiveCount();
        // 同时进入池中的最大线程数 (有锁)
        int largestPoolSize = threadPoolExecutor.getLargestPoolSize();
        // 线程池中执行任务总数量 (有锁)
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();

        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.poolSize = poolSize;
        this.activeCount = activeCount;
        this.largestPoolSize = largestPoolSize;
        this.completedTaskCount = completedTaskCount;
    }

    public void initQueueProperty(BlockingQueue<Runnable> queue) {
        // 队列元素个数
        int queueSize = queue.size();
        // 队列类型
        String queueType = queue.getClass().getSimpleName();
        // 队列剩余容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列容量
        int queueCapacity = queueSize + remainingCapacity;

        this.queueName = queueType;
        this.capacity = queueCapacity;
        this.queueSize = queueSize;
        this.remainingCapacity = remainingCapacity;
    }
}
