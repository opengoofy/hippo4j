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

package cn.hippo4j.adapter.web.undertow;

import cn.hippo4j.adapter.web.IWebThreadPoolHandlerSupport;
import cn.hippo4j.common.constant.ChangeThreadPoolConstants;
import cn.hippo4j.common.extension.enums.WebContainerEnum;
import cn.hippo4j.common.model.ThreadPoolBaseInfo;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.util.ReflectionUtils;
import org.xnio.Options;
import org.xnio.XnioWorker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;

/**
 * The supporting class for WebThreadPoolHandler,
 * which facilitates the creation of a Undertow web container.
 */
@Slf4j
public class UndertowWebThreadPoolHandlerSupport implements IWebThreadPoolHandlerSupport {

    private final AbstractThreadPoolRuntime runtime;

    private Executor executor;

    public UndertowWebThreadPoolHandlerSupport(AbstractThreadPoolRuntime runtime) {
        this.runtime = runtime;
    }

    /**
     * A callback will be invoked and the Executor will be set up when the web container has been started.
     * @param executor Thread-pool executor in Undertow container.
     */
    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    private final long noRejectCount = -1L;

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
            poolBaseInfo.setRejectedName("RejectedExecutionException");
            poolBaseInfo.setQueueType("org.jboss.threads.EnhancedQueueExecutor.TaskNode:FIFO");

            EnhancedQueueExecutor enhancedQueueExecutor =
                    (EnhancedQueueExecutor) ReflectUtil.getFieldValue(
                            ReflectUtil.getFieldValue(xnioWorker, "taskPool"), "executor");

            Method getMaximumQueueSize = ReflectionUtils.findMethod(enhancedQueueExecutor.getClass(), "getMaximumQueueSize");
            ReflectionUtils.makeAccessible(getMaximumQueueSize);
            int queueCapacity = (int) ReflectionUtils.invokeMethod(getMaximumQueueSize, enhancedQueueExecutor);
            poolBaseInfo.setQueueCapacity(queueCapacity);
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
            long keepAliveTime = xnioWorker.getOption(Options.WORKER_TASK_KEEPALIVE);
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

        Field executorFiled = ReflectionUtils.findField(fieldObject.getClass(), "executor");
        ReflectionUtils.makeAccessible(executorFiled);
        EnhancedQueueExecutor enhancedQueueExecutor = (EnhancedQueueExecutor) ReflectionUtils.getField(executorFiled, fieldObject);

        Method getLargestPoolSize = ReflectionUtils.findMethod(enhancedQueueExecutor.getClass(), "getLargestPoolSize");
        ReflectionUtils.makeAccessible(getLargestPoolSize);
        int largestPoolSize = (int) ReflectionUtils.invokeMethod(getLargestPoolSize, enhancedQueueExecutor);

        Method getQueueSize = ReflectionUtils.findMethod(enhancedQueueExecutor.getClass(), "getQueueSize");
        ReflectionUtils.makeAccessible(getQueueSize);
        int queueSize = (int) ReflectionUtils.invokeMethod(getQueueSize, enhancedQueueExecutor);

        Method getMaximumQueueSize = ReflectionUtils.findMethod(enhancedQueueExecutor.getClass(), "getMaximumQueueSize");
        ReflectionUtils.makeAccessible(getMaximumQueueSize);
        int queueCapacity = (int) ReflectionUtils.invokeMethod(getMaximumQueueSize, enhancedQueueExecutor);

        int remainingCapacity = queueCapacity - queueSize;

        Method getCompletedTaskCount = ReflectionUtils.findMethod(enhancedQueueExecutor.getClass(), "getCompletedTaskCount");
        ReflectionUtils.makeAccessible(getCompletedTaskCount);
        long completedTaskCount = (long) ReflectionUtils.invokeMethod(getCompletedTaskCount, enhancedQueueExecutor);

        Method getHandoffExecutor = ReflectionUtils.findMethod(enhancedQueueExecutor.getClass(), "getHandoffExecutor");
        ReflectionUtils.makeAccessible(getHandoffExecutor);
        Executor handoffExecutor = (Executor) ReflectionUtils.invokeMethod(getHandoffExecutor, enhancedQueueExecutor);

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
                : -noRejectCount;
        stateInfo.setRejectCount(rejectCount);
        stateInfo.setClientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        stateInfo.setTimestamp(System.currentTimeMillis());

        stateInfo.setQueueType("org.jboss.threads.EnhancedQueueExecutor.TaskNode:FIFO");
        stateInfo.setQueueSize(queueSize);
        stateInfo.setQueueCapacity(queueCapacity);
        stateInfo.setQueueRemainingCapacity(remainingCapacity);
        stateInfo.setLargestPoolSize(largestPoolSize);
        stateInfo.setCompletedTaskCount(completedTaskCount);
        stateInfo.setRejectedName(handoffExecutor.getClass().getName());

        return runtime.supplement(stateInfo);
    }

    @Override
    public void updateWebThreadPool(ThreadPoolParameterInfo threadPoolParameterInfo) {
        try {
            XnioWorker xnioWorker = (XnioWorker) executor;
            Integer coreSize = threadPoolParameterInfo.corePoolSizeAdapt();
            Integer maxSize = threadPoolParameterInfo.maximumPoolSizeAdapt();
            int keepAliveTime = threadPoolParameterInfo.getKeepAliveTime().intValue();
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
