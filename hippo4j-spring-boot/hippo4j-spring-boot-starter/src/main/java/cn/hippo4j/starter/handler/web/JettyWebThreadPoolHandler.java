package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolParameterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : wh
 * @date : 2022/2/28 16:55
 * @description:
 */
@Slf4j
public class JettyWebThreadPoolHandler extends AbstractWebThreadPoolService{



    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        JettyWebServer jettyWebServer = (JettyWebServer) webServer;
        return jettyWebServer.getServer().getThreadPool();
    }

    @Override
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {
        try {
            ThreadPoolExecutor jettyExecutor = (ThreadPoolExecutor) executor;
            int originalCoreSize = jettyExecutor.getCorePoolSize();
            int originalMaximumPoolSize = jettyExecutor.getMaximumPoolSize();
            long originalKeepAliveTime = jettyExecutor.getKeepAliveTime(TimeUnit.SECONDS);

            jettyExecutor.setCorePoolSize(poolParameterInfo.getCoreSize());
            jettyExecutor.setMaximumPoolSize(poolParameterInfo.getMaxSize());
            jettyExecutor.setKeepAliveTime(poolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS);

            log.info(
                    "🔥 Changed web thread pool. coreSize :: [{}], maxSize :: [{}], keepAliveTime :: [{}]",
                    String.format("%s => %s", originalCoreSize, poolParameterInfo.getCoreSize()),
                    String.format("%s => %s", originalMaximumPoolSize, poolParameterInfo.getMaxSize()),
                    String.format("%s => %s", originalKeepAliveTime, poolParameterInfo.getKeepAliveTime())
            );
        } catch (Exception ex) {
            log.error("Failed to modify the jetty thread pool parameter.", ex);
        }

    }
}
