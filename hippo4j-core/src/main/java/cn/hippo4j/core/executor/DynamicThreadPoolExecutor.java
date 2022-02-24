package cn.hippo4j.core.executor;

import cn.hippo4j.core.executor.support.AbstractDynamicExecutorSupport;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dynamic threadPool wrap.
 *
 * @author chen.ma
 * @date 2021/7/8 21:47
 */
public class DynamicThreadPoolExecutor extends AbstractDynamicExecutorSupport {

    @Getter
    @Setter
    private TaskDecorator taskDecorator;

    @Getter
    @Setter
    private RejectedExecutionHandler redundancyHandler;

    @Getter
    private final String threadPoolId;

    @Getter
    private final AtomicLong rejectCount = new AtomicLong();

    public DynamicThreadPoolExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit unit,
                                     boolean waitForTasksToCompleteOnShutdown,
                                     long awaitTerminationMillis,
                                     @NonNull BlockingQueue<Runnable> workQueue,
                                     @NonNull String threadPoolId,
                                     @NonNull ThreadFactory threadFactory,
                                     @NonNull RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, waitForTasksToCompleteOnShutdown, awaitTerminationMillis, workQueue, threadPoolId, threadFactory, handler);
        this.threadPoolId = threadPoolId;

        // Number of dynamic proxy denial policies.
        RejectedExecutionHandler rejectedProxy = RejectedProxyUtil.createProxy(handler, threadPoolId, rejectCount);
        setRejectedExecutionHandler(rejectedProxy);

        // Redundant fields to avoid reflecting the acquired fields when sending change information.
        redundancyHandler = handler;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (taskDecorator != null) {
            command = taskDecorator.decorate(command);
        }

        super.execute(command);
    }

    @Override
    protected ExecutorService initializeExecutor() {
        return this;
    }

    /**
     * Get reject count.
     *
     * @return
     */
    public Long getRejectCountNum() {
        return rejectCount.get();
    }

}
