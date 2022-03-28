package cn.hippo4j.core.executor.web;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.toolkit.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * @author : wh
 * @date : 2022/2/28 16:55
 * @description:
 */
@Slf4j
public class JettyWebThreadPoolHandler extends AbstractWebThreadPoolService {

    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        JettyWebServer jettyWebServer = (JettyWebServer) webServer;
        return jettyWebServer.getServer().getThreadPool();
    }

    @Override
    public PoolBaseInfo simpleInfo() {
        PoolBaseInfo poolBaseInfo = new PoolBaseInfo();
        QueuedThreadPool queuedThreadPool = (QueuedThreadPool) executor;
        poolBaseInfo.setCoreSize(queuedThreadPool.getMinThreads());
        poolBaseInfo.setMaximumSize(queuedThreadPool.getMaxThreads());

        BlockingQueue jobs = (BlockingQueue) ReflectUtil.getFieldValue(queuedThreadPool, "_jobs");
        int queueCapacity = jobs.remainingCapacity() + jobs.size();

        poolBaseInfo.setQueueCapacity(queueCapacity);
        poolBaseInfo.setQueueType(jobs.getClass().getSimpleName());
        poolBaseInfo.setKeepAliveTime((long) queuedThreadPool.getIdleTimeout());
        poolBaseInfo.setRejectedName("RejectedExecutionException");
        return poolBaseInfo;
    }

    @Override
    public PoolParameter getWebThreadPoolParameter() {
        PoolParameterInfo parameterInfo = null;
        try {
            parameterInfo = new PoolParameterInfo();
            QueuedThreadPool jettyExecutor = (QueuedThreadPool) executor;

            int minThreads = jettyExecutor.getMinThreads();
            int maxThreads = jettyExecutor.getMaxThreads();

            parameterInfo.setCoreSize(minThreads);
            parameterInfo.setMaxSize(maxThreads);
        } catch (Exception ex) {
            log.error("Failed to get the jetty thread pool parameter.", ex);
        }

        return parameterInfo;
    }

    @Override
    public PoolRunStateInfo getWebRunStateInfo() {
        return null;
    }

    @Override
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {
        try {
            QueuedThreadPool jettyExecutor = (QueuedThreadPool) executor;

            int minThreads = jettyExecutor.getMinThreads();
            int maxThreads = jettyExecutor.getMaxThreads();

            Integer coreSize = poolParameterInfo.getCoreSize();
            Integer maxSize = poolParameterInfo.getMaxSize();

            jettyExecutor.setMinThreads(coreSize);
            jettyExecutor.setMaxThreads(maxSize);

            log.info(
                    "[JETTY] Changed web thread pool. " +
                            "\n    coreSize :: [{}]" +
                            "\n    maxSize :: [{}]",
                    String.format("%s => %s", minThreads, jettyExecutor.getMinThreads()),
                    String.format("%s => %s", maxThreads, jettyExecutor.getMaxThreads())
            );
        } catch (Exception ex) {
            log.error("Failed to modify the jetty thread pool parameter.", ex);
        }
    }

}
