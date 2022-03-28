package cn.hippo4j.core.executor.web;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.toolkit.CalculateUtil;
import cn.hutool.core.date.DateUtil;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.util.ReflectionUtils;
import org.xnio.Options;
import org.xnio.XnioWorker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
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
    public PoolBaseInfo simpleInfo() {
        PoolBaseInfo poolBaseInfo = new PoolBaseInfo();
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

        return poolBaseInfo;
    }

    @Override
    public PoolParameter getWebThreadPoolParameter() {
        PoolParameterInfo parameterInfo = null;
        try {
            parameterInfo = new PoolParameterInfo();
            XnioWorker xnioWorker = (XnioWorker) executor;
            int minThreads = xnioWorker.getOption(Options.WORKER_TASK_CORE_THREADS);
            int maxThreads = xnioWorker.getOption(Options.WORKER_TASK_MAX_THREADS);
            int keepAliveTime = xnioWorker.getOption(Options.WORKER_TASK_KEEPALIVE);

            parameterInfo.setCoreSize(minThreads);
            parameterInfo.setMaxSize(maxThreads);
            parameterInfo.setKeepAliveTime(keepAliveTime);
        } catch (Exception ex) {
            log.error("Failed to get the undertow thread pool parameter.", ex);
        }

        return parameterInfo;
    }

    @Override
    public PoolRunStateInfo getWebRunStateInfo() {
        PoolRunStateInfo stateInfo = new PoolRunStateInfo();
        XnioWorker xnioWorker = (XnioWorker) executor;

        // private final TaskPool taskPool;
        Field field = ReflectionUtils.findField(XnioWorker.class, "taskPool");
        ReflectionUtils.makeAccessible(field);
        Object fieldObject = ReflectionUtils.getField(field, xnioWorker);
        // 核心线程数
        Method getCorePoolSize = ReflectionUtils.findMethod(fieldObject.getClass(), "getCorePoolSize");
        ReflectionUtils.makeAccessible(getCorePoolSize);
        int corePoolSize = (int) ReflectionUtils.invokeMethod(getCorePoolSize, fieldObject);
        // 最大线程数
        Method getMaximumPoolSize = ReflectionUtils.findMethod(fieldObject.getClass(), "getMaximumPoolSize");
        ReflectionUtils.makeAccessible(getMaximumPoolSize);
        int maximumPoolSize = (int) ReflectionUtils.invokeMethod(getMaximumPoolSize, fieldObject);
        // 线程池当前线程数 (有锁)
        Method getPoolSize = ReflectionUtils.findMethod(fieldObject.getClass(), "getPoolSize");
        ReflectionUtils.makeAccessible(getPoolSize);
        int poolSize = (int) ReflectionUtils.invokeMethod(getPoolSize, fieldObject);
        // 活跃线程数 (有锁)
        Method getActiveCount = ReflectionUtils.findMethod(fieldObject.getClass(), "getActiveCount");
        ReflectionUtils.makeAccessible(getActiveCount);
        int activeCount = (int) ReflectionUtils.invokeMethod(getActiveCount, fieldObject);
        activeCount = (activeCount <= 0) ? 0 : activeCount;
        // 当前负载
        String currentLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "";
        // 峰值负载
        // 没有峰值记录，直接使用当前数据
        String peakLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "";

        stateInfo.setCoreSize(corePoolSize);
        stateInfo.setPoolSize(poolSize);
        stateInfo.setMaximumSize(maximumPoolSize);
        stateInfo.setActiveSize(activeCount);
        stateInfo.setCurrentLoad(currentLoad);
        stateInfo.setPeakLoad(peakLoad);

        long rejectCount = fieldObject instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) fieldObject).getRejectCountNum() : -1L;
        stateInfo.setRejectCount(rejectCount);
        stateInfo.setClientLastRefreshTime(DateUtil.formatDateTime(new Date()));
        stateInfo.setTimestamp(System.currentTimeMillis());
        return stateInfo;
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
                    "[UNDERTOW] Changed web thread pool. " +
                            "\n    coreSize :: [{}]" +
                            "\n    maxSize :: [{}]" +
                            "\n    keepAliveTime :: [{}]",
                    String.format("%s => %s", originalCoreSize, coreSize),
                    String.format("%s => %s", originalMaximumPoolSize, maxSize),
                    String.format("%s => %s", originalKeepAliveTime, keepAliveTime)
            );

        } catch (Exception ex) {
            log.error("Failed to modify the undertow thread pool parameter.", ex);
        }
    }

}
