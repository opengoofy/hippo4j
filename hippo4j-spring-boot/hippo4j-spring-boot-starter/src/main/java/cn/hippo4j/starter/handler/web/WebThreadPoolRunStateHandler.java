package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.starter.handler.AbstractThreadPoolRuntime;
import cn.hippo4j.starter.toolkit.ByteConvertUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;

import java.util.concurrent.*;

/**
 * Web thread pool run state handler.
 *
 * @author chen.ma
 * @date 2022/1/19 21:05
 */
public class WebThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    @Override
    public PoolBaseInfo simpleInfo(Executor executor) {
        PoolBaseInfo poolBaseInfo = new PoolBaseInfo();
        if (executor != null && executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
            RejectedExecutionHandler rejectedExecutionHandler = threadPoolExecutor.getRejectedExecutionHandler();
            long keepAliveTime = threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS);

            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            int queueSize = queue.size();
            int remainingCapacity = queue.remainingCapacity();
            int queueCapacity = queueSize + remainingCapacity;

            poolBaseInfo.setCoreSize(corePoolSize);
            poolBaseInfo.setMaximumSize(maximumPoolSize);
            poolBaseInfo.setKeepAliveTime(keepAliveTime);
            poolBaseInfo.setQueueType(queue.getClass().getSimpleName());
            poolBaseInfo.setQueueCapacity(queueCapacity);
            poolBaseInfo.setRejectedName(rejectedExecutionHandler.getClass().getSimpleName());
        }
        return poolBaseInfo;
    }

    @Override
    protected PoolRunStateInfo supplement(PoolRunStateInfo poolRunStateInfo) {
        // 内存占比: 使用内存 / 最大内存
        RuntimeInfo runtimeInfo = new RuntimeInfo();
        String memoryProportion = StrUtil.builder(
                "已分配: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getTotalMemory()),
                " / 最大可用: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getMaxMemory())
        ).toString();

        poolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad() + "%");
        poolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad() + "%");

        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(runtimeInfo.getFreeMemory()));

        return poolRunStateInfo;
    }

}
