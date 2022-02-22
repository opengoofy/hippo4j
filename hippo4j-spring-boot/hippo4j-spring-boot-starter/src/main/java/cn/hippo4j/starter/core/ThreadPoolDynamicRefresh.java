package cn.hippo4j.starter.core;

import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.starter.alarm.ThreadPoolAlarmManage;
import cn.hippo4j.starter.toolkit.thread.QueueTypeEnum;
import cn.hippo4j.starter.toolkit.thread.RejectedTypeEnum;
import cn.hippo4j.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
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
        PoolParameterInfo parameter = JSONUtil.parseObject(content, PoolParameterInfo.class);
        ThreadPoolAlarmManage.sendPoolConfigChange(parameter);
        ThreadPoolDynamicRefresh.refreshDynamicPool(parameter);
    }

    public static void refreshDynamicPool(PoolParameterInfo parameter) {
        String threadPoolId = parameter.getTpId();
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        String originalQuery = executor.getQueue().getClass().getSimpleName();
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.SECONDS);
        String originalRejected = executor.getRejectedExecutionHandler().getClass().getSimpleName();
        boolean originalAllowCoreThreadTimeOut = executor.allowsCoreThreadTimeOut();

        changePoolInfo(executor, parameter);
        ThreadPoolExecutor afterExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        log.info(
                "[🔥 {}] Changed thread pool. \ncoreSize :: [{}], maxSize :: [{}], queueType :: [{}], capacity :: [{}], keepAliveTime :: [{}], rejectedType :: [{}], allowCoreThreadTimeOut :: [{}]",
                threadPoolId.toUpperCase(),
                String.format("%s => %s", originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format("%s => %s", originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format("%s => %s", originalQuery, QueueTypeEnum.getBlockingQueueNameByType(parameter.getQueueType())),
                String.format("%s => %s", originalCapacity,
                        (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format("%s => %s", originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.SECONDS)),
                String.format("%s => %s", originalRejected, RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType())),
                String.format("%s => %s", originalAllowCoreThreadTimeOut, EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()))
        );
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
                log.warn("The queue length cannot be modified. Queue type mismatch. Current queue type :: {}", executor.getQueue().getClass().getSimpleName());
            }
        }

        if (parameter.getKeepAliveTime() != null) {
            executor.setKeepAliveTime(parameter.getKeepAliveTime(), TimeUnit.SECONDS);
        }

        if (parameter.getRejectedType() != null) {
            executor.setRejectedExecutionHandler(RejectedTypeEnum.createPolicy(parameter.getRejectedType()));
        }

        if (parameter.getAllowCoreThreadTimeOut() != null) {
            executor.allowCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()));
        }
    }

}
