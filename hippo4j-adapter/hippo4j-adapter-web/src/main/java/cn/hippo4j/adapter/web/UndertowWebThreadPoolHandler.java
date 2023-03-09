/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.adapter.web;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Executor;

import cn.hippo4j.common.constant.ChangeThreadPoolConstants;
import cn.hippo4j.common.enums.WebContainerEnum;
import cn.hippo4j.common.model.ThreadPoolBaseInfo;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.xnio.Options;
import org.xnio.XnioWorker;

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.util.ReflectionUtils;

/**
 * Undertow web thread pool handler.
 */
@Slf4j
public class UndertowWebThreadPoolHandler extends AbstractWebThreadPoolService {

    private static final String UNDERTOW_NAME = "undertow";

    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        // There is no need to consider reflection performance because the fetch is a singleton.
        // Springboot 2-3 version, can directly through reflection to obtain the undertow property
        UndertowServletWebServer undertowServletWebServer = (UndertowServletWebServer) webServer;
        Field undertowField = ReflectionUtils.findField(UndertowServletWebServer.class, UNDERTOW_NAME);
        ReflectionUtils.makeAccessible(undertowField);

        Undertow undertow = (Undertow) ReflectionUtils.getField(undertowField, undertowServletWebServer);
        return Objects.isNull(undertow) ? null : undertow.getWorker();
    }

    @Override
    public ThreadPoolBaseInfo simpleInfo() {
        ThreadPoolBaseInfo poolBaseInfo = new ThreadPoolBaseInfo();
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
    public ThreadPoolParameter getWebThreadPoolParameter() {
        ThreadPoolParameterInfo parameterInfo = null;
        try {
            parameterInfo = new ThreadPoolParameterInfo();
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
    public ThreadPoolRunStateInfo getWebRunStateInfo() {
        ThreadPoolRunStateInfo stateInfo = new ThreadPoolRunStateInfo();
        XnioWorker xnioWorker = (XnioWorker) executor;
        Field field = ReflectionUtils.findField(XnioWorker.class, "taskPool");
        ReflectionUtils.makeAccessible(field);
        Object fieldObject = ReflectionUtils.getField(field, xnioWorker);
        Method getCorePoolSize = ReflectionUtils.findMethod(fieldObject.getClass(), "getCorePoolSize");
        ReflectionUtils.makeAccessible(getCorePoolSize);
        int corePoolSize = (int) ReflectionUtils.invokeMethod(getCorePoolSize, fieldObject);
        Method getMaximumPoolSize = ReflectionUtils.findMethod(fieldObject.getClass(), "getMaximumPoolSize");
        ReflectionUtils.makeAccessible(getMaximumPoolSize);
        int maximumPoolSize = (int) ReflectionUtils.invokeMethod(getMaximumPoolSize, fieldObject);
        Method getPoolSize = ReflectionUtils.findMethod(fieldObject.getClass(), "getPoolSize");
        ReflectionUtils.makeAccessible(getPoolSize);
        int poolSize = (int) ReflectionUtils.invokeMethod(getPoolSize, fieldObject);
        Method getActiveCount = ReflectionUtils.findMethod(fieldObject.getClass(), "getActiveCount");
        ReflectionUtils.makeAccessible(getActiveCount);
        int activeCount = (int) ReflectionUtils.invokeMethod(getActiveCount, fieldObject);
        activeCount = Math.max(activeCount, 0);
        String currentLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "";
        String peakLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "";
        stateInfo.setCoreSize(corePoolSize);
        stateInfo.setPoolSize(poolSize);
        stateInfo.setMaximumSize(maximumPoolSize);
        stateInfo.setActiveSize(activeCount);
        stateInfo.setCurrentLoad(currentLoad);
        stateInfo.setPeakLoad(peakLoad);
        long rejectCount = fieldObject instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) fieldObject).getRejectCountNum()
                : -1L;
        stateInfo.setRejectCount(rejectCount);
        stateInfo.setClientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        stateInfo.setTimestamp(System.currentTimeMillis());
        return stateInfo;
    }

    @Override
    public void updateWebThreadPool(ThreadPoolParameterInfo threadPoolParameterInfo) {
        try {
            XnioWorker xnioWorker = (XnioWorker) executor;
            Integer coreSize = threadPoolParameterInfo.corePoolSizeAdapt();
            Integer maxSize = threadPoolParameterInfo.maximumPoolSizeAdapt();
            Integer keepAliveTime = threadPoolParameterInfo.getKeepAliveTime();
            int originalCoreSize = xnioWorker.getOption(Options.WORKER_TASK_CORE_THREADS);
            int originalMaximumPoolSize = xnioWorker.getOption(Options.WORKER_TASK_MAX_THREADS);
            int originalKeepAliveTime = xnioWorker.getOption(Options.WORKER_TASK_KEEPALIVE);
            xnioWorker.setOption(Options.WORKER_TASK_CORE_THREADS, coreSize);
            xnioWorker.setOption(Options.WORKER_TASK_MAX_THREADS, maxSize);
            xnioWorker.setOption(Options.WORKER_TASK_KEEPALIVE, keepAliveTime);
            log.info("[Undertow] Changed web thread pool. corePoolSize: {}, maximumPoolSize: {}, keepAliveTime: {}",
                    String.format(ChangeThreadPoolConstants.CHANGE_DELIMITER, originalCoreSize, coreSize),
                    String.format(ChangeThreadPoolConstants.CHANGE_DELIMITER, originalMaximumPoolSize, maxSize),
                    String.format(ChangeThreadPoolConstants.CHANGE_DELIMITER, originalKeepAliveTime, keepAliveTime));
        } catch (Exception ex) {
            log.error("Failed to modify the undertow thread pool parameter.", ex);
        }
    }

    @Override
    public WebContainerEnum getWebContainerType() {
        return WebContainerEnum.UNDERTOW;
    }
}
