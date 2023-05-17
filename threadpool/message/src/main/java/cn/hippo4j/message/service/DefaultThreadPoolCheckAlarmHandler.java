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

package cn.hippo4j.message.service;

import cn.hippo4j.core.api.ThreadPoolCheckAlarm;
import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.core.toolkit.ExecutorTraceContextUtil;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Default thread-pool check alarm handler.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultThreadPoolCheckAlarmHandler implements Runnable, ThreadPoolCheckAlarm {

    private final Hippo4jSendMessageService hippo4jSendMessageService;

    @Value("${spring.profiles.active:UNKNOWN}")
    private String active;

    @Value("${spring.dynamic.thread-pool.item-id:}")
    private String itemId;

    @Value("${spring.application.name:UNKNOWN}")
    private String applicationName;

    @Value("${spring.dynamic.thread-pool.check-state-interval:5}")
    private Integer checkStateInterval;

    private final ScheduledExecutorService alarmNotifyExecutor = new ScheduledThreadPoolExecutor(
            1,
            r -> new Thread(r, "client.alarm.notify"));

    private final ExecutorService asyncAlarmNotifyExecutor = ThreadPoolBuilder.builder()
            .poolThreadSize(2, 4)
            .threadFactory("client.execute.timeout.alarm")
            .allowCoreThreadTimeOut(true)
            .keepAliveTime(60L, TimeUnit.SECONDS)
            .workQueue(new LinkedBlockingQueue(4096))
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    @Override
    public void run(String... args) throws Exception {
        alarmNotifyExecutor.scheduleWithFixedDelay(this, 0, checkStateInterval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        List<String> listThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
        listThreadPoolId.forEach(threadPoolId -> {
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(threadPoolId);
            if (threadPoolNotifyAlarm != null && threadPoolNotifyAlarm.getAlarm()) {
                DynamicThreadPoolWrapper wrapper = GlobalThreadPoolManage.getExecutorService(threadPoolId);
                ThreadPoolExecutor executor = wrapper.getExecutor();
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
            hippo4jSendMessageService.sendAlarmMessage(NotifyTypeEnum.CAPACITY, alarmNotifyRequest);
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
            hippo4jSendMessageService.sendAlarmMessage(NotifyTypeEnum.ACTIVITY, alarmNotifyRequest);
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
            ThreadPoolExecutor threadPoolExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();
            if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
                AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyRequest(threadPoolExecutor);
                alarmNotifyRequest.setThreadPoolId(threadPoolId);
                hippo4jSendMessageService.sendAlarmMessage(NotifyTypeEnum.REJECT, alarmNotifyRequest);
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
        if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
            try {
                AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyRequest(threadPoolExecutor);
                alarmNotifyRequest.setThreadPoolId(threadPoolId);
                alarmNotifyRequest.setExecuteTime(executeTime);
                alarmNotifyRequest.setExecuteTimeOut(executeTimeOut);
                String executeTimeoutTrace = ExecutorTraceContextUtil.getAndRemoveTimeoutTrace();
                if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                    alarmNotifyRequest.setExecuteTimeoutTrace(executeTimeoutTrace);
                }
                Runnable task = () -> hippo4jSendMessageService.sendAlarmMessage(NotifyTypeEnum.TIMEOUT, alarmNotifyRequest);
                asyncAlarmNotifyExecutor.execute(task);
            } catch (Throwable ex) {
                log.error("Send thread pool execution timeout alarm error.", ex);
            }
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
        long rejectCount = threadPoolExecutor instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) threadPoolExecutor).getRejectCountNum()
                : -1L;
        return AlarmNotifyRequest.builder()
                .appName(StringUtil.isBlank(itemId) ? applicationName : itemId)
                .active(active.toUpperCase())
                .identify(IdentifyUtil.getIdentify())
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
