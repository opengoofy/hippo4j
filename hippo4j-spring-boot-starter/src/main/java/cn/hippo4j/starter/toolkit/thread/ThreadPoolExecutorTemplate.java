package cn.hippo4j.starter.toolkit.thread;

import cn.hippo4j.starter.toolkit.ArrayUtil;

import java.util.concurrent.*;

/**
 * ThreadPool executor template.
 *
 * @author chen.ma
 * @date 2021/7/5 21:59
 */
public class ThreadPoolExecutorTemplate extends ThreadPoolExecutor {

    public ThreadPoolExecutorTemplate(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue,
                                      ThreadFactory threadFactory,
                                      RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    private Exception clientTrace() {
        return new Exception("Tread task root stack trace.");
    }

    @Override
    public void execute(final Runnable command) {
        super.execute(wrap(command, clientTrace()));
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return super.submit(wrap(task, clientTrace()));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return super.submit(wrap(task, clientTrace()));
    }

    private Runnable wrap(final Runnable task, final Exception clientStack) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.setStackTrace(ArrayUtil.addAll(clientStack.getStackTrace(), e.getStackTrace()));
                throw e;
            }
        };
    }

    private <T> Callable<T> wrap(final Callable<T> task, final Exception clientStack) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                e.setStackTrace(ArrayUtil.addAll(clientStack.getStackTrace(), e.getStackTrace()));
                throw e;
            }
        };
    }

}
