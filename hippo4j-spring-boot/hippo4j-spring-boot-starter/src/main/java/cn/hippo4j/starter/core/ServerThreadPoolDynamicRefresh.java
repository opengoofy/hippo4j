package cn.hippo4j.starter.core;

import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.AbstractDynamicExecutorSupport;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.RejectedTypeEnum;
import cn.hippo4j.core.executor.support.ResizableCapacityLinkedBlockIngQueue;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread pool dynamic refresh.
 *
 * @author chen.ma
 * @date 2021/6/20 15:51
 */
@Slf4j
@AllArgsConstructor
public class ServerThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh {

    private final ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler;

    @Override
    public void dynamicRefresh(String content) {
        PoolParameterInfo parameter = JSONUtil.parseObject(content, PoolParameterInfo.class);

        String threadPoolId = parameter.getTpId();
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        refreshDynamicPool(parameter, executor);
    }

    /**
     * Refresh dynamic pool.
     *
     * @param parameter
     * @param executor
     */
    public void refreshDynamicPool(PoolParameter parameter, ThreadPoolExecutor executor) {
        String threadPoolId = parameter.getTpId();
        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        String originalQuery = executor.getQueue().getClass().getSimpleName();
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.SECONDS);
        boolean originalAllowCoreThreadTimeOut = executor.allowsCoreThreadTimeOut();

        Long originalExecuteTimeOut = null;
        RejectedExecutionHandler rejectedExecutionHandler = executor.getRejectedExecutionHandler();
        if (executor instanceof AbstractDynamicExecutorSupport) {
            DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
            rejectedExecutionHandler = dynamicExecutor.getRedundancyHandler();
            originalExecuteTimeOut = dynamicExecutor.getExecuteTimeOut();
        }
        String originalRejected = rejectedExecutionHandler.getClass().getSimpleName();

        // Send change message.
        ChangeParameterNotifyRequest request = new ChangeParameterNotifyRequest();
        request.setBeforeCorePoolSize(originalCoreSize);
        request.setBeforeMaximumPoolSize(originalMaximumPoolSize);
        request.setBeforeAllowsCoreThreadTimeOut(originalAllowCoreThreadTimeOut);
        request.setBeforeKeepAliveTime(originalKeepAliveTime);
        request.setBlockingQueueName(originalQuery);
        request.setBeforeQueueCapacity(originalCapacity);
        request.setBeforeRejectedName(originalRejected);
        request.setBeforeExecuteTimeOut(originalExecuteTimeOut);
        request.setThreadPoolId(threadPoolId);

        changePoolInfo(executor, parameter);
        ThreadPoolExecutor afterExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        request.setNowCorePoolSize(afterExecutor.getCorePoolSize());
        request.setNowMaximumPoolSize(afterExecutor.getMaximumPoolSize());
        request.setNowAllowsCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()));
        request.setNowKeepAliveTime(afterExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        request.setNowQueueCapacity((afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size()));
        request.setNowRejectedName(RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType()));
        request.setNowExecuteTimeOut(originalExecuteTimeOut);
        threadPoolNotifyAlarmHandler.sendPoolConfigChange(request);

        log.info(
                "[{}] Changed thread pool. " +
                        "\n    coreSize :: [{}]" +
                        "\n    maxSize :: [{}]" +
                        "\n    queueType :: [{}]" +
                        "\n    capacity :: [{}]" +
                        "\n    keepAliveTime :: [{}]" +
                        "\n    executeTimeOut :: [{}]" +
                        "\n    rejectedType :: [{}]" +
                        "\n    allowCoreThreadTimeOut :: [{}]",
                threadPoolId.toUpperCase(),
                String.format("%s => %s", originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format("%s => %s", originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format("%s => %s", originalQuery, QueueTypeEnum.getBlockingQueueNameByType(parameter.getQueueType())),
                String.format("%s => %s", originalCapacity,
                        (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format("%s => %s", originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.SECONDS)),
                String.format("%s => %s", originalExecuteTimeOut, originalExecuteTimeOut),
                String.format("%s => %s", originalRejected, RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType())),
                String.format("%s => %s", originalAllowCoreThreadTimeOut, EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()))
        );
    }

    /**
     * Change pool info.
     *
     * @param executor
     * @param parameter
     */
    public void changePoolInfo(ThreadPoolExecutor executor, PoolParameter parameter) {
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
            RejectedExecutionHandler rejectedExecutionHandler = RejectedTypeEnum.createPolicy(parameter.getRejectedType());
            if (executor instanceof AbstractDynamicExecutorSupport) {
                DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
                dynamicExecutor.setRedundancyHandler(rejectedExecutionHandler);
                AtomicLong rejectCount = dynamicExecutor.getRejectCount();
                rejectedExecutionHandler = RejectedProxyUtil.createProxy(rejectedExecutionHandler, parameter.getTpId(), rejectCount);
            }

            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }

        if (parameter.getAllowCoreThreadTimeOut() != null) {
            executor.allowCoreThreadTimeOut(EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()));
        }
    }

}
