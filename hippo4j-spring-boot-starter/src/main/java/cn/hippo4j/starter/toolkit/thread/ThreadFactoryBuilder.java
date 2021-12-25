package cn.hippo4j.starter.toolkit.thread;

import cn.hippo4j.common.design.builder.Builder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ThreadFactory builder.
 *
 * @author chen.ma
 * @date 2021/7/5 21:53
 */
public class ThreadFactoryBuilder implements Builder<ThreadFactory> {

    private static final long serialVersionUID = 1L;

    /**
     * 用于线程创建的线程工厂类
     */
    private ThreadFactory backingThreadFactory;

    /**
     * 线程名的前缀
     */
    private String namePrefix;

    /**
     * 是否守护线程，默认false
     */
    private Boolean daemon;

    /**
     * 线程优先级
     */
    private Integer priority;

    /**
     * 未捕获异常处理器
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * 设置用于创建基础线程的线程工厂.
     *
     * @param backingThreadFactory 用于创建基础线程的线程工厂
     * @return this
     */
    public ThreadFactoryBuilder threadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    /**
     * 设置线程名前缀, 例如设置前缀为 mb-thread- 则线程名为 mb-thread-1 之类.
     *
     * @param namePrefix 线程名前缀
     * @return this
     */
    public ThreadFactoryBuilder prefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    /**
     * 设置是否守护线程.
     *
     * @param daemon 是否守护线程
     * @return this
     */
    public ThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * 设置线程优先级.
     *
     * @param priority 优先级
     * @return this
     * @see Thread#MIN_PRIORITY
     * @see Thread#NORM_PRIORITY
     * @see Thread#MAX_PRIORITY
     */
    public ThreadFactoryBuilder priority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(String.format("Thread priority ({}) must be >= {}", priority, Thread.MIN_PRIORITY));
        }
        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format("Thread priority ({}) must be <= {}", priority, Thread.MAX_PRIORITY));
        }
        this.priority = priority;
        return this;
    }

    /**
     * 设置未捕获异常的处理方式.
     *
     * @param uncaughtExceptionHandler {@link Thread.UncaughtExceptionHandler}
     */
    public void uncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    /**
     * 构建.
     *
     * @return
     */
    public static ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }

    /**
     * 构建 {@link ThreadFactory}
     *
     * @return {@link ThreadFactory}
     */
    @Override
    public ThreadFactory build() {
        return build(this);
    }

    /**
     * 构建.
     *
     * @param builder {@link ThreadFactoryBuilder}
     * @return {@link ThreadFactory}
     */
    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        final ThreadFactory backingThreadFactory = (null != builder.backingThreadFactory)
                ? builder.backingThreadFactory
                : Executors.defaultThreadFactory();
        final String namePrefix = builder.namePrefix;
        final Boolean daemon = builder.daemon;
        final Integer priority = builder.priority;
        final Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
        final AtomicLong count = (null == namePrefix) ? null : new AtomicLong();
        return r -> {
            final Thread thread = backingThreadFactory.newThread(r);
            if (null != namePrefix) {
                thread.setName(namePrefix + "-" + count.getAndIncrement());
            }
            if (null != daemon) {
                thread.setDaemon(daemon);
            }
            if (null != priority) {
                thread.setPriority(priority);
            }
            if (null != handler) {
                thread.setUncaughtExceptionHandler(handler);
            }
            return thread;
        };
    }

}
