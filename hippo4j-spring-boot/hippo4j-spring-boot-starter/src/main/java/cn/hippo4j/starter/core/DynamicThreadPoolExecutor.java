package cn.hippo4j.starter.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.core.task.TaskDecorator;

import java.lang.reflect.Proxy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dynamic threadPool wrap.
 *
 * @author chen.ma
 * @date 2021/7/8 21:47
 */
public class DynamicThreadPoolExecutor extends DynamicExecutorConfigurationSupport {

    @Getter
    @Setter
    private TaskDecorator taskDecorator;

    @Getter
    private final String threadPoolId;

    private final AtomicInteger rejectCount = new AtomicInteger();

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

        RejectedExecutionHandler rejectedProxy = (RejectedExecutionHandler) Proxy
                .newProxyInstance(
                        handler.getClass().getClassLoader(),
                        new Class[]{RejectedExecutionHandler.class},
                        new RejectedProxyInvocationHandler(handler, rejectCount));
        setRejectedExecutionHandler(rejectedProxy);
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
    public Integer getRejectCount() {
        return rejectCount.get();
    }

}
