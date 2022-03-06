package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.starter.handler.AbstractThreadPoolRuntime;
import cn.hippo4j.starter.toolkit.ByteConvertUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.xnio.Options;
import org.xnio.XnioWorker;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Web thread pool run state handler.
 *
 * @author chen.ma
 * @date 2022/1/19 21:05
 */
@Slf4j
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
        } else if (Objects.equals("QueuedThreadPool", executor.getClass().getSimpleName())) {
            QueuedThreadPool queuedThreadPool = (QueuedThreadPool) executor;
            poolBaseInfo.setCoreSize(queuedThreadPool.getMinThreads());
            poolBaseInfo.setMaximumSize(queuedThreadPool.getMaxThreads());

            BlockingQueue jobs = (BlockingQueue) ReflectUtil.getFieldValue(queuedThreadPool, "_jobs");
            int queueCapacity = jobs.remainingCapacity() + jobs.size();

            poolBaseInfo.setQueueCapacity(queueCapacity);
            poolBaseInfo.setQueueType(jobs.getClass().getSimpleName());
            poolBaseInfo.setKeepAliveTime((long) queuedThreadPool.getIdleTimeout());
            poolBaseInfo.setRejectedName("RejectedExecutionException");
        } else if (Objects.equals("NioXnioWorker", executor.getClass().getSimpleName())) {
            XnioWorker xnioWorker = (XnioWorker) executor;
            try {
                int coreSize = xnioWorker.getOption(Options.WORKER_TASK_CORE_THREADS);
                int maximumPoolSize = xnioWorker.getOption(Options.WORKER_TASK_MAX_THREADS);
                int keepAliveTime = xnioWorker.getOption(Options.WORKER_TASK_KEEPALIVE);

                poolBaseInfo.setCoreSize(coreSize);
                poolBaseInfo.setMaximumSize(maximumPoolSize);
                poolBaseInfo.setKeepAliveTime((long) keepAliveTime);
                poolBaseInfo.setRejectedName("-");
                poolBaseInfo.setQueueType("-");
            } catch (Exception ex) {
                log.error("The undertow container failed to get thread pool parameters.", ex);
            }
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
