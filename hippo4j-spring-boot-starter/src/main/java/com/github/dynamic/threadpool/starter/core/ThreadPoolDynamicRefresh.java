package com.github.dynamic.threadpool.starter.core;

import com.alibaba.fastjson.JSON;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.alarm.ThreadPoolAlarmManage;
import com.github.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.RejectedTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool dynamic refresh.
 *
 * @author chen.ma
 * @date 2021/6/20 15:51
 */
@Slf4j
public class ThreadPoolDynamicRefresh {

    public static void refreshDynamicPool(String content) {
        PoolParameterInfo parameter = JSON.parseObject(content, PoolParameterInfo.class);
        ThreadPoolAlarmManage.sendPoolConfigChange(parameter);
        ThreadPoolDynamicRefresh.refreshDynamicPool(parameter);
    }

    public static void refreshDynamicPool(PoolParameterInfo parameter) {
        String threadPoolId = parameter.getTpId();
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getPool();

        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        int originalQueryType = parameter.getQueueType();
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.MILLISECONDS);
        int originalRejectedType = parameter.getRejectedType();

        changePoolInfo(executor, parameter);
        ThreadPoolExecutor afterExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getPool();

        log.info("[🔥 {}] Changed thread pool. coreSize :: [{}], maxSize :: [{}], queueType :: [{}], capacity :: [{}], keepAliveTime :: [{}], rejectedType :: [{}]",
                threadPoolId.toUpperCase(),
                String.format("%s=>%s", originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format("%s=>%s", originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format("%s=>%s", originalQueryType, parameter.getQueueType()),
                String.format("%s=>%s", originalCapacity,
                        (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format("%s=>%s", originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS)),
                String.format("%s=>%s", originalRejectedType, parameter.getRejectedType()));
    }

    public static void changePoolInfo(ThreadPoolExecutor executor, PoolParameterInfo parameter) {
        if (parameter.getCoreSize() != null) {
            executor.setCorePoolSize(parameter.getCoreSize());
        }

        if (parameter.getMaxSize() != null) {
            executor.setMaximumPoolSize(parameter.getMaxSize());
        }

        if (parameter.getCapacity() != null
                && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.type, parameter.getQueueType())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockIngQueue) {
                ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) executor.getQueue();
                queue.setCapacity(parameter.getCapacity());
            } else {
                log.warn("[Pool change] The queue length cannot be modified. Queue type mismatch. Current queue type :: {}", executor.getQueue().getClass().getSimpleName());
            }
        }

        if (parameter.getKeepAliveTime() != null) {
            executor.setKeepAliveTime(parameter.getKeepAliveTime(), TimeUnit.SECONDS);
        }

        if (parameter.getRejectedType() != null) {
            executor.setRejectedExecutionHandler(RejectedTypeEnum.createPolicy(parameter.getRejectedType()));
        }
    }

}
