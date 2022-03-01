package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolParameterInfo;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.util.ReflectionUtils;
import org.xnio.Options;
import org.xnio.XnioWorker;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Undertow web thread pool handler.
 *
 * @author chen.ma
 * @date 2022/1/19 21:19
 */
@Slf4j
public class UndertowWebThreadPoolHandler extends AbstractWebThreadPoolService {

    private static final String UNDERTOW_NAME = "undertow";

    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        // There is no need to consider reflection performance because the fetch is a singleton
        UndertowWebServer undertowWebServer = (UndertowWebServer) webServer;
        Field undertowField = ReflectionUtils.findField(UndertowWebServer.class, UNDERTOW_NAME);
        ReflectionUtils.makeAccessible(undertowField);
        Undertow undertow = (Undertow) ReflectionUtils.getField(undertowField, undertowWebServer);
        return Objects.isNull(undertow) ? null : undertow.getWorker();
    }

    @Override
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {
        try {
            XnioWorker xnioWorker = (XnioWorker) executor;

            Integer coreSize = poolParameterInfo.getCoreSize();
            Integer maxSize = poolParameterInfo.getMaxSize();
            Integer keepAliveTime = poolParameterInfo.getKeepAliveTime();

            int originalCoreSize = xnioWorker.getOption(Options.WORKER_TASK_CORE_THREADS);
            int originalMaximumPoolSize = xnioWorker.getOption(Options.WORKER_TASK_MAX_THREADS);
            int originalKeepAliveTime = xnioWorker.getOption(Options.WORKER_TASK_KEEPALIVE);

            xnioWorker.setOption(Options.WORKER_TASK_CORE_THREADS, coreSize);
            xnioWorker.setOption(Options.WORKER_TASK_MAX_THREADS, maxSize);
            xnioWorker.setOption(Options.WORKER_TASK_KEEPALIVE, keepAliveTime);
            log.info(
                    "🔥 Changed web thread pool. coreSize :: [{}], maxSize :: [{}], keepAliveTime :: [{}]",
                    String.format("%s => %s", originalCoreSize, coreSize),
                    String.format("%s => %s", originalMaximumPoolSize, maxSize),
                    String.format("%s => %s", originalKeepAliveTime, keepAliveTime)
            );

        } catch (Exception ex) {
            log.error("Failed to modify the undertow thread pool parameter.", ex);
        }
    }

}
