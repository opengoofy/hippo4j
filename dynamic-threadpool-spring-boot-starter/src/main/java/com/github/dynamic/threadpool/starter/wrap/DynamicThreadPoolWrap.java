package com.github.dynamic.threadpool.starter.wrap;

import com.github.dynamic.threadpool.starter.common.CommonThreadPool;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import lombok.Data;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Dynamic threadPool wrap.
 *
 * @author chen.ma
 * @date 2021/6/20 16:55
 */
@Data
public class DynamicThreadPoolWrap {

    private String tenantId;

    private String itemId;

    private String tpId;

    private boolean subscribeFlag;

    private CustomThreadPoolExecutor pool;

    /**
     * 首选服务端线程池, 为空使用默认线程池 {@link CommonThreadPool#getInstance(String)}
     *
     * @param threadPoolId
     */
    public DynamicThreadPoolWrap(String threadPoolId) {
        this(threadPoolId, CommonThreadPool.getInstance(threadPoolId));
    }

    /**
     * 首选服务端线程池, 为空使用 threadPoolExecutor
     *
     * @param threadPoolId
     * @param threadPoolExecutor
     */
    public DynamicThreadPoolWrap(String threadPoolId, CustomThreadPoolExecutor threadPoolExecutor) {
        this.tpId = threadPoolId;
        this.pool = threadPoolExecutor;
    }

    /**
     * 提交任务
     *
     * @param command
     */
    public void execute(Runnable command) {
        pool.execute(command);
    }

    /**
     * 提交任务
     *
     * @param task
     * @return
     */
    public Future<?> submit(Runnable task) {
        return pool.submit(task);
    }

    /**
     * 提交任务
     *
     * @param task
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Callable<T> task) {
        return pool.submit(task);
    }

}
