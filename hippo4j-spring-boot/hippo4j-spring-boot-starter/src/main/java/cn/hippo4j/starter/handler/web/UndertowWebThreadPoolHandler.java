package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolParameterInfo;
import io.undertow.Undertow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        assert undertowField != null;
        ReflectionUtils.makeAccessible(undertowField);
        Undertow undertow = (Undertow) ReflectionUtils.getField(undertowField, undertowWebServer);
        return Objects.isNull(undertow) ? null : undertow.getWorker();
    }

    @Override
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {
        try {
            ThreadPoolExecutor undertowExecutor = (ThreadPoolExecutor) executor;
            int originalCoreSize = undertowExecutor.getCorePoolSize();
            int originalMaximumPoolSize = undertowExecutor.getMaximumPoolSize();
            long originalKeepAliveTime = undertowExecutor.getKeepAliveTime(TimeUnit.SECONDS);

            undertowExecutor.setCorePoolSize(poolParameterInfo.getCoreSize());
            undertowExecutor.setMaximumPoolSize(poolParameterInfo.getMaxSize());
            undertowExecutor.setKeepAliveTime(poolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS);

            log.info(
                    "🔥 Changed web thread pool. coreSize :: [{}], maxSize :: [{}], keepAliveTime :: [{}]",
                    String.format("%s => %s", originalCoreSize, poolParameterInfo.getCoreSize()),
                    String.format("%s => %s", originalMaximumPoolSize, poolParameterInfo.getMaxSize()),
                    String.format("%s => %s", originalKeepAliveTime, poolParameterInfo.getKeepAliveTime())
            );
        } catch (Exception ex) {
            log.error("Failed to modify the undertow thread pool parameter.", ex);
        }

    }

}
