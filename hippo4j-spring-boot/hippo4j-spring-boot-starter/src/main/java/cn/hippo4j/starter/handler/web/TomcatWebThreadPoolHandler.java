package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolParameterInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tomcat web thread pool handler.
 *
 * @author chen.ma
 * @date 2022/1/19 20:57
 */
@Slf4j
@AllArgsConstructor
public class TomcatWebThreadPoolHandler extends AbstractWebThreadPoolService{


    private final AtomicBoolean cacheFlag = new AtomicBoolean(Boolean.FALSE);

    private static String EXCEPTION_MESSAGE;

    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        if (cacheFlag.get()) {
            log.warn("Exception getting Tomcat thread pool. Exception message :: {}", EXCEPTION_MESSAGE);
            return null;
        }

        Executor tomcatExecutor = null;
        try {
            tomcatExecutor = ((TomcatWebServer) webServer).getTomcat().getConnector().getProtocolHandler().getExecutor();
        } catch (Exception ex) {
            cacheFlag.set(Boolean.TRUE);
            EXCEPTION_MESSAGE = ex.getMessage();
            log.error("Failed to get Tomcat thread pool. Message :: {}", EXCEPTION_MESSAGE);
        }

        return tomcatExecutor;
    }

    @Override
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {
        try {
            ThreadPoolExecutor tomcatExecutor = (ThreadPoolExecutor) executor;
            tomcatExecutor.setCorePoolSize(poolParameterInfo.getCoreSize());
            tomcatExecutor.setMaximumPoolSize(poolParameterInfo.getMaxSize());
            tomcatExecutor.setKeepAliveTime(poolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS);
            int originalCoreSize = tomcatExecutor.getCorePoolSize();
            int originalMaximumPoolSize = tomcatExecutor.getMaximumPoolSize();
            long originalKeepAliveTime = tomcatExecutor.getKeepAliveTime(TimeUnit.SECONDS);
            log.info(
                    "🔥 Changed web thread pool. coreSize :: [{}], maxSize :: [{}], keepAliveTime :: [{}]",
                    String.format("%s => %s", originalCoreSize, poolParameterInfo.getCoreSize()),
                    String.format("%s => %s", originalMaximumPoolSize, poolParameterInfo.getMaxSize()),
                    String.format("%s => %s", originalKeepAliveTime, poolParameterInfo.getKeepAliveTime())
            );


        } catch (Exception ex) {
            log.error("Failed to modify the Tomcat thread pool parameter.", ex);
        }
    }

}
