package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolParameterInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.boot.web.server.WebServer;

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
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {
        try {
            ThreadPool.SizedThreadPool jettyExecutor = (ThreadPool.SizedThreadPool) executor;

            Integer coreSize = poolParameterInfo.getCoreSize();
            Integer maxSize = poolParameterInfo.getMaxSize();
            jettyExecutor.setMinThreads(coreSize);
            jettyExecutor.setMaxThreads(maxSize);

            log.info(
                    "🔥 Changed web thread pool. coreSize :: [{}], maxSize :: [{}]",
                    String.format("%s => %s", jettyExecutor.getMinThreads(), coreSize),
                    String.format("%s => %s", jettyExecutor.getMaxThreads(), maxSize)
            );
        } catch (Exception ex) {
            log.error("Failed to modify the jetty thread pool parameter.", ex);
        }
    }

}
