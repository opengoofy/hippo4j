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

package cn.hippo4j.core.executor;

import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.ExecutorTraceContextUtil;
import cn.hippo4j.message.service.Hippo4jSendMessageService;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Thread-pool notify alarm handler.
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolNotifyAlarmHandler implements Runnable, CommandLineRunner {

    @NonNull
    private final Hippo4jSendMessageService hippo4jSendMessageService;

    @Value("${spring.profiles.active:UNKNOWN}")
    private String active;

    @Value("${spring.dynamic.thread-pool.item-id:}")
    private String itemId;

    @Value("${spring.application.name:UNKNOWN}")
    private String applicationName;

    @Value("${spring.dynamic.thread-pool.check-state-interval:5}")
    private Integer checkStateInterval;

    private final ScheduledExecutorService ALARM_NOTIFY_EXECUTOR = new ScheduledThreadPoolExecutor(
            1,
            r -> new Thread(r, "client.alarm.notify"));

    private final ExecutorService ASYNC_ALARM_NOTIFY_EXECUTOR = ThreadPoolBuilder.builder()
            .poolThreadSize(2, 4)
            .threadFactory("client.execute.timeout.alarm")
            .allowCoreThreadTimeOut(true)
            .keepAliveTime(60L, TimeUnit.SECONDS)
            .workQueue(new LinkedBlockingQueue(4096))
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    @Override
    public void run(String... args) throws Exception {
        ALARM_NOTIFY_EXECUTOR.scheduleWithFixedDelay(this, 0, checkStateInterval, TimeUnit.SECONDS);
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
     * @param threadPoolId
     * @param threadPoolExecutor
     */
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
     * @param threadPoolId
     * @param threadPoolExecutor
     */
    public void checkPoolActivityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolNotifyAlarm alarmConfig = GlobalNotifyAlarmManage.get(threadPoolId);
        if (Objects.isNull(alarmConfig) || !alarmConfig.getAlarm() || alarmConfig.getCapacityAlarm() <= 0) {
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
     * @param threadPoolId
     */
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
        ASYNC_ALARM_NOTIFY_EXECUTOR.execute(checkPoolRejectedAlarmTask);
    }

    /**
     * Async send execute time out alarm.
     *
     * @param threadPoolId
     * @param executeTime
     * @param executeTimeOut
     * @param threadPoolExecutor
     */
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
                ASYNC_ALARM_NOTIFY_EXECUTOR.execute(task);
            } catch (Throwable ex) {
                log.error("Send thread pool execution timeout alarm error.", ex);
            }
        }
    }

    /**
     * Send pool config change.
     *
     * @param request
     */
    public void sendPoolConfigChange(ChangeParameterNotifyRequest request) {
        request.setActive(active.toUpperCase());
        String appName = StringUtil.isBlank(itemId) ? applicationName : itemId;
        request.setAppName(appName);
        request.setIdentify(IdentifyUtil.getIdentify());
        hippo4jSendMessageService.sendChangeMessage(request);
    }

    /**
     * Build alarm notify request.
     *
     * @param threadPoolExecutor
     * @return
     */
    public AlarmNotifyRequest buildAlarmNotifyRequest(ThreadPoolExecutor threadPoolExecutor) {
        BlockingQueue<Runnable> blockingQueue = threadPoolExecutor.getQueue();
        RejectedExecutionHandler rejectedExecutionHandler = threadPoolExecutor instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) threadPoolExecutor).getRedundancyHandler()
                : threadPoolExecutor.getRejectedExecutionHandler();
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
