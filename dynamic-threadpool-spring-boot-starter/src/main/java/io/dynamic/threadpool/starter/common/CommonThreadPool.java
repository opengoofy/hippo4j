package io.dynamic.threadpool.starter.common;

import io.dynamic.threadpool.common.enums.QueueTypeEnum;
import io.dynamic.threadpool.starter.builder.ThreadPoolBuilder;
import io.dynamic.threadpool.starter.toolkit.thread.RejectedPolicies;

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
                .threadFactory(threadPoolId)
                .corePoolNum(3)
                .maxPoolNum(5)
                .capacity(512)
                .keepAliveTime(10000L)
                .timeUnit(TimeUnit.SECONDS)
                .isFastPool(false)
                .rejected(RejectedPolicies.runsOldestTaskPolicy())
                .workQueue(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE)
                .build();
        return poolExecutor;
    }
}
