package com.github.dynamic.threadpool.starter.common;

import com.github.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;
import com.github.dynamic.threadpool.starter.toolkit.thread.RejectedPolicies;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 公共线程池生产者
 *
 * @author chen.ma
 * @date 2021/6/16 22:35
 */
public class CommonThreadPool {

    public static ThreadPoolExecutor getInstance(String threadPoolId) {
        ThreadPoolExecutor poolExecutor = ThreadPoolBuilder.builder()
                .isCustomPool(true)
                .threadFactory(threadPoolId)
                .poolThreadSize(3, 5)
                .keepAliveTime(1000L, TimeUnit.SECONDS)
                .rejected(RejectedPolicies.runsOldestTaskPolicy())
                .workQueue(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, 512)
                .build();
        return poolExecutor;
    }

}
