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

package cn.hippo4j.threadpool.alarm.handler;

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.alarm.api.ThreadPoolCheckAlarm;
import cn.hippo4j.threadpool.alarm.toolkit.ExecutorTraceContextUtil;
import cn.hippo4j.threadpool.message.core.request.AlarmNotifyRequest;
import cn.hippo4j.threadpool.message.api.NotifyTypeEnum;
import cn.hippo4j.threadpool.message.core.service.GlobalNotifyAlarmManage;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolSendMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.hippo4j.common.propertie.EnvironmentProperties.active;
import static cn.hippo4j.common.propertie.EnvironmentProperties.applicationName;
import static cn.hippo4j.common.propertie.EnvironmentProperties.checkStateInterval;
import static cn.hippo4j.common.propertie.EnvironmentProperties.itemId;
import static cn.hippo4j.common.propertie.IdentifyProperties.IDENTIFY;

/**
 * Default thread-pool check alarm handler.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultThreadPoolCheckAlarmHandler implements Runnable, ThreadPoolCheckAlarm {

    private final ThreadPoolSendMessageService threadPoolSendMessageService;

    private final ScheduledExecutorService alarmNotifyExecutor = new ScheduledThreadPoolExecutor(
            1,
            r -> new Thread(r, "client.alarm.notify"));

    private final ExecutorService asyncAlarmNotifyExecutor = new ThreadPoolExecutor(
            2,
            4,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(4096),
            new ThreadFactory() {

                private final AtomicInteger count = new AtomicInteger();

                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable, "client.execute.timeout.alarm_" + count.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void scheduleExecute() {
        alarmNotifyExecutor.scheduleWithFixedDelay(this, 0, checkStateInterval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        List<String> listThreadPoolId = ThreadPoolExecutorRegistry.listThreadPoolExecutorId();
        listThreadPoolId.forEach(threadPoolId -> {
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(threadPoolId);
            if (threadPoolNotifyAlarm != null && threadPoolNotifyAlarm.getAlarm()) {
                ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
                ThreadPoolExecutor executor = executorHolder.getExecutor();
                checkPoolCapacityAlarm(threadPoolId, executor);
                checkPoolActivityAlarm(threadPoolId, executor);
            }
        });
    }

    /**
     * Check thread pool capacity alarm.
     *
     * @param threadPoolId       thread-pool id
     * @param threadPoolExecutor thread-pool executor
     */
    @Override
    public void checkPoolCapacityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolNotifyAlarm alarmConfig = GlobalNotifyAlarmManage.get(threadPoolId);
        if (Objects.isNull(alarmConfig) || !alarmConfig.getAlarm() || alarmConfig.getCapacityAlarm() <= 0) {
            return;
        }
        BlockingQueue blockingQueue = threadPoolExecutor.getQueue();
        int queueSize = blockingQueue.size();
        int capacity = queueSize + blockingQueue.remainingCapacity();
        int divide = CalculateUtil.divide(queueSize, capacity);
        boolean isSend = alarmConfig.getAlarm() && divide > alarmConfig.getCapacityAlarm();
        if (isSend) {
            AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyRequest(threadPoolExecutor);
            alarmNotifyRequest.setThreadPoolId(threadPoolId);
            threadPoolSendMessageService.sendAlarmMessage(NotifyTypeEnum.CAPACITY, alarmNotifyRequest);
        }
    }

    /**
     * Check thread pool activity alarm.
     *
     * @param threadPoolId       thread-pool id
     * @param threadPoolExecutor thread-pool executor
     */
    @Override
    public void checkPoolActivityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolNotifyAlarm alarmConfig = GlobalNotifyAlarmManage.get(threadPoolId);
        if (Objects.isNull(alarmConfig) || !alarmConfig.getAlarm() || alarmConfig.getActiveAlarm() <= 0) {
            return;
        }
        int activeCount = threadPoolExecutor.getActiveCount();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int divide = CalculateUtil.divide(activeCount, maximumPoolSize);
        boolean isSend = alarmConfig.getAlarm() && divide > alarmConfig.getActiveAlarm();
        if (isSend) {
            AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyRequest(threadPoolExecutor);
            alarmNotifyRequest.setThreadPoolId(threadPoolId);
            threadPoolSendMessageService.sendAlarmMessage(NotifyTypeEnum.ACTIVITY, alarmNotifyRequest);
        }
    }

    /**
     * Async send rejected alarm.
     *
     * @param threadPoolId thread-pool id
     */
    @Override
    public void asyncSendRejectedAlarm(String threadPoolId) {
        Runnable checkPoolRejectedAlarmTask = () -> {
            ThreadPoolNotifyAlarm alarmConfig = GlobalNotifyAlarmManage.get(threadPoolId);
            if (Objects.isNull(alarmConfig) || !alarmConfig.getAlarm()) {
                return;
            }
            ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorRegistry.getHolder(threadPoolId).getExecutor();
            if (Objects.equals(threadPoolExecutor.getClass().getName(), "cn.hippo4j.core.executor.DynamicThreadPoolExecutor")) {
                AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyRequest(threadPoolExecutor);
                alarmNotifyRequest.setThreadPoolId(threadPoolId);
                threadPoolSendMessageService.sendAlarmMessage(NotifyTypeEnum.REJECT, alarmNotifyRequest);
            }
        };
        asyncAlarmNotifyExecutor.execute(checkPoolRejectedAlarmTask);
    }

    /**
     * Async send execute time out alarm.
     *
     * @param threadPoolId       thread-pool id
     * @param executeTime        execute time
     * @param executeTimeOut     execute time-out
     * @param threadPoolExecutor thread-pool executor
     */
    @Override
    public void asyncSendExecuteTimeOutAlarm(String threadPoolId, long executeTime, long executeTimeOut, ThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolNotifyAlarm alarmConfig = GlobalNotifyAlarmManage.get(threadPoolId);
        if (Objects.isNull(alarmConfig) || !alarmConfig.getAlarm()) {
            return;
        }
        try {
            AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyRequest(threadPoolExecutor);
            alarmNotifyRequest.setThreadPoolId(threadPoolId);
            alarmNotifyRequest.setExecuteTime(executeTime);
            alarmNotifyRequest.setExecuteTimeOut(executeTimeOut);
            String executeTimeoutTrace = ExecutorTraceContextUtil.getAndRemoveTimeoutTrace();
            if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                alarmNotifyRequest.setExecuteTimeoutTrace(executeTimeoutTrace);
            }
            Runnable task = () -> threadPoolSendMessageService.sendAlarmMessage(NotifyTypeEnum.TIMEOUT, alarmNotifyRequest);
            asyncAlarmNotifyExecutor.execute(task);
        } catch (Throwable ex) {
            log.error("Send thread pool execution timeout alarm error.", ex);
        }
    }

    /**
     * Build alarm notify request.
     *
     * @param threadPoolExecutor thread-pool executor
     * @return
     */
    public AlarmNotifyRequest buildAlarmNotifyRequest(ThreadPoolExecutor threadPoolExecutor) {
        BlockingQueue<Runnable> blockingQueue = threadPoolExecutor.getQueue();
        RejectedExecutionHandler rejectedExecutionHandler = threadPoolExecutor.getRejectedExecutionHandler();
        long rejectCount = -1L;
        if (Objects.equals(threadPoolExecutor.getClass().getName(), "cn.hippo4j.core.executor.DynamicThreadPoolExecutor")) {
            Object actualRejectCountNum = ReflectUtil.invoke(threadPoolExecutor, "getRejectCountNum");
            if (actualRejectCountNum != null) {
                rejectCount = (long) actualRejectCountNum;
            }
        }
        return AlarmNotifyRequest.builder()
                .appName(StringUtil.isBlank(itemId) ? applicationName : itemId)
                .active(active.toUpperCase())
                .identify(IDENTIFY)
                .corePoolSize(threadPoolExecutor.getCorePoolSize())
                .maximumPoolSize(threadPoolExecutor.getMaximumPoolSize())
                .poolSize(threadPoolExecutor.getPoolSize())
                .activeCount(threadPoolExecutor.getActiveCount())
                .largestPoolSize(threadPoolExecutor.getLargestPoolSize())
                .completedTaskCount(threadPoolExecutor.getCompletedTaskCount())
                .queueName(blockingQueue.getClass().getSimpleName())
                .capacity(blockingQueue.size() + blockingQueue.remainingCapacity())
                .queueSize(blockingQueue.size())
                .remainingCapacity(blockingQueue.remainingCapacity())
                .rejectedExecutionHandlerName(rejectedExecutionHandler.getClass().getSimpleName())
                .rejectCountNum(rejectCount)
                .build();
    }
}
