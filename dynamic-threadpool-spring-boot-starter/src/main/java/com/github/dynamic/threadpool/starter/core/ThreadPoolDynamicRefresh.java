package com.github.dynamic.threadpool.starter.core;

import com.alibaba.fastjson.JSON;
import com.github.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.toolkit.thread.RejectedTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池动态刷新
 *
 * @author chen.ma
 * @date 2021/6/20 15:51
 */
@Slf4j
public class ThreadPoolDynamicRefresh {

    public static void refreshDynamicPool(String content) {
        PoolParameterInfo parameter = JSON.parseObject(content, PoolParameterInfo.class);
        String tpId = parameter.getTpId();
        Integer coreSize = parameter.getCoreSize(), maxSize = parameter.getMaxSize(),
                queueType = parameter.getQueueType(), capacity = parameter.getCapacity(),
                keepAliveTime = parameter.getKeepAliveTime(), rejectedType = parameter.getRejectedType();
        refreshDynamicPool(tpId, coreSize, maxSize, queueType, capacity, keepAliveTime, rejectedType);
    }

    public static void refreshDynamicPool(String threadPoolId, Integer coreSize, Integer maxSize, Integer queueType, Integer capacity, Integer keepAliveTime, Integer rejectedType) {
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getPool();

        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        int originalQueryType = queueType;
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.MILLISECONDS);
        int originalRejectedType = rejectedType;

        changePoolInfo(executor, coreSize, maxSize, queueType, capacity, keepAliveTime, rejectedType);
        ThreadPoolExecutor afterExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getPool();

        log.info("[🔥 {}] Changed thread pool. coreSize :: [{}], maxSize :: [{}], queueType :: [{}], capacity :: [{}], keepAliveTime :: [{}], rejectedType :: [{}]",
                threadPoolId.toUpperCase(),
                String.format("%s=>%s", originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format("%s=>%s", originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format("%s=>%s", originalQueryType, queueType),
                String.format("%s=>%s", originalCapacity, (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format("%s=>%s", originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS)),
                String.format("%s=>%s", originalRejectedType, rejectedType));
    }

    public static void changePoolInfo(ThreadPoolExecutor executor, Integer coreSize, Integer maxSize, Integer queueType, Integer capacity, Integer keepAliveTime, Integer rejectedType) {
        if (coreSize != null) {
            executor.setCorePoolSize(coreSize);
        }

        if (maxSize != null) {
            executor.setMaximumPoolSize(maxSize);
        }

        if (capacity != null && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.type, queueType)) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockIngQueue) {
                ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) executor.getQueue();
                queue.setCapacity(capacity);
            } else {
                log.warn("[Pool change] The queue length cannot be modified. Queue type mismatch. Current queue type :: {}", executor.getQueue().getClass().getSimpleName());
            }
        }

        if (keepAliveTime != null) {
            executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
        }

        if (rejectedType != null) {
            executor.setRejectedExecutionHandler(RejectedTypeEnum.createPolicy(queueType));
        }
    }

}
