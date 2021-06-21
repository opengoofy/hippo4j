package io.dynamic.threadpool.starter.common;

import java.util.concurrent.*;

/**
 * 公共线程池生产者
 *
 * @author chen.ma
 * @date 2021/6/16 22:35
 */
public class CommonThreadPool {

    public static ThreadPoolExecutor getInstance(String threadPoolId) {
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue workQueue = new LinkedBlockingQueue(512);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                3,
                5,
                10000,
                unit,
                workQueue,
                r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(false);
                    thread.setName(threadPoolId);
                    return thread;
                });
        return poolExecutor;
    }
}
