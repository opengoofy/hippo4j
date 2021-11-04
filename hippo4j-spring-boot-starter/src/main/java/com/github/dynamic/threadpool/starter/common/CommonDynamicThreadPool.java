package com.github.dynamic.threadpool.starter.common;

import com.github.dynamic.threadpool.starter.core.DynamicThreadPoolExecutor;
import com.github.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.RejectedPolicies;
import com.github.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Common dynamic threadPool.
 *
 * @author chen.ma
 * @date 2021/6/16 22:35
 */
public class CommonDynamicThreadPool {

    public static DynamicThreadPoolExecutor getInstance(String threadPoolId) {
        DynamicThreadPoolExecutor poolExecutor = (DynamicThreadPoolExecutor) ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .poolThreadSize(3, 5)
                .keepAliveTime(1000L, TimeUnit.SECONDS)
                .rejected(RejectedPolicies.runsOldestTaskPolicy())
                .alarmConfig(1, 80, 80)
                .workQueue(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, 512)
                .build();
        return poolExecutor;
    }

}
