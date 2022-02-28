package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.PoolParameterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Abstract web thread pool service.
 *
 * @author chen.ma
 * @date 2022/1/19 21:20
 */
@Slf4j
public abstract class AbstractWebThreadPoolService implements WebThreadPoolService {

    /**
     * Thread pool executor.
     */
    protected volatile Executor executor;

    /**
     * Get web thread pool by server.
     *
     * @return
     */
    protected abstract Executor getWebThreadPoolByServer(WebServer webServer);



    @Override
    public Executor getWebThreadPool() {
        if (executor == null) {
            synchronized (AbstractWebThreadPoolService.class) {
                if (executor == null) {
                    ApplicationContext applicationContext = ApplicationContextHolder.getInstance();
                    WebServer webServer = ((WebServerApplicationContext) applicationContext).getWebServer();
                    executor = getWebThreadPoolByServer(webServer);
                }
            }
        }

        return executor;
    }

    public void log(PoolParameterInfo poolParameterInfo, ThreadPoolExecutor threadPoolExecutor) {
        int originalCoreSize = threadPoolExecutor.getCorePoolSize();
        int originalMaximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        long originalKeepAliveTime = threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS);
        log.info(
                "🔥 Changed web thread pool. coreSize :: [{}], maxSize :: [{}], keepAliveTime :: [{}]",
                String.format("%s => %s", originalCoreSize, poolParameterInfo.getCoreSize()),
                String.format("%s => %s", originalMaximumPoolSize, poolParameterInfo.getMaxSize()),
                String.format("%s => %s", originalKeepAliveTime, poolParameterInfo.getKeepAliveTime())
        );

    }

}
