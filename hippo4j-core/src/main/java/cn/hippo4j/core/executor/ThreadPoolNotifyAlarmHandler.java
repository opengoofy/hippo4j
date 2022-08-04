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
import cn.hippo4j.core.toolkit.TraceContextUtil;
import cn.hippo4j.message.service.Hippo4jSendMessageService;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Thread pool alarm notify.
 *
 * @author chen.ma
 * @date 2021/8/15 14:13
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolNotifyAlarmHandler implements Runnable, CommandLineRunner {

    @NonNull
    private final Hippo4jSendMessageService hippoSendMessageService;

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

    private final ExecutorService EXECUTE_TIMEOUT_EXECUTOR = ThreadPoolBuilder.builder()
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
            if (threadPoolNotifyAlarm != null && threadPoolNotifyAlarm.getIsAlarm()) {
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
        if (hippoSendMessageService == null) {
            return;
        }
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(threadPoolId);
        BlockingQueue blockIngQueue = threadPoolExecutor.getQueue();
        int queueSize = blockIngQueue.size();
        int capacity = queueSize + blockIngQueue.remainingCapacity();
        int divide = CalculateUtil.divide(queueSize, capacity);
        boolean isSend = threadPoolNotifyAlarm.getIsAlarm()
                && divide > threadPoolNotifyAlarm.getCapacityAlarm();
        if (isSend) {
            AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyReq(threadPoolExecutor);
            alarmNotifyRequest.setThreadPoolId(threadPoolId);
            hippoSendMessageService.sendAlarmMessage(NotifyTypeEnum.CAPACITY, alarmNotifyRequest);
        }
    }

    /**
     * Check thread pool activity alarm.
     *
     * @param threadPoolId
     * @param threadPoolExecutor
     */
    public void checkPoolActivityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        int activeCount = threadPoolExecutor.getActiveCount();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int divide = CalculateUtil.divide(activeCount, maximumPoolSize);
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(threadPoolId);
        boolean isSend = threadPoolNotifyAlarm.getIsAlarm()
                && divide > threadPoolNotifyAlarm.getActiveAlarm();
        if (isSend) {
            AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyReq(threadPoolExecutor);
            alarmNotifyRequest.setThreadPoolId(threadPoolId);
            hippoSendMessageService.sendAlarmMessage(NotifyTypeEnum.ACTIVITY, alarmNotifyRequest);
        }
    }

    /**
     * Check pool rejected alarm.
     *
     * @param threadPoolId
     */
    public void checkPoolRejectedAlarm(String threadPoolId) {
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(threadPoolId);
        if (Objects.isNull(threadPoolNotifyAlarm) || !threadPoolNotifyAlarm.getIsAlarm()) {
            return;
        }
        ThreadPoolExecutor threadPoolExecutor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();
        checkPoolRejectedAlarm(threadPoolId, threadPoolExecutor);
    }

    /**
     * Check pool rejected alarm.
     *
     * @param threadPoolId
     * @param threadPoolExecutor
     */
    public void checkPoolRejectedAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
            AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyReq(threadPoolExecutor);
            alarmNotifyRequest.setThreadPoolId(threadPoolId);
            hippoSendMessageService.sendAlarmMessage(NotifyTypeEnum.REJECT, alarmNotifyRequest);
        }
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
        ThreadPoolNotifyAlarm threadPoolNotifyAlarm = GlobalNotifyAlarmManage.get(threadPoolId);
        if (Objects.isNull(threadPoolNotifyAlarm) || !threadPoolNotifyAlarm.getIsAlarm()) {
            return;
        }
        if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
            try {
                AlarmNotifyRequest alarmNotifyRequest = buildAlarmNotifyReq(threadPoolExecutor);
                alarmNotifyRequest.setThreadPoolId(threadPoolId);
                alarmNotifyRequest.setExecuteTime(executeTime);
                alarmNotifyRequest.setExecuteTimeOut(executeTimeOut);
                String executeTimeoutTrace = TraceContextUtil.getAndRemove();
                if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                    alarmNotifyRequest.setExecuteTimeoutTrace(executeTimeoutTrace);
                }
                Runnable task = () -> hippoSendMessageService.sendAlarmMessage(NotifyTypeEnum.TIMEOUT, alarmNotifyRequest);
                EXECUTE_TIMEOUT_EXECUTOR.execute(task);
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
        String appName = StrUtil.isBlank(itemId) ? applicationName : itemId;
        request.setAppName(appName);
        request.setIdentify(IdentifyUtil.getIdentify());
        hippoSendMessageService.sendChangeMessage(request);
    }

    /**
     * Build alarm notify req.
     *
     * @param threadPoolExecutor
     * @return
     */
    public AlarmNotifyRequest buildAlarmNotifyReq(ThreadPoolExecutor threadPoolExecutor) {
        AlarmNotifyRequest request = new AlarmNotifyRequest();
        String appName = StrUtil.isBlank(itemId) ? applicationName : itemId;
        request.setAppName(appName);
        // 核心线程数
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        // 最大线程数
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        // 线程池当前线程数 (有锁)
        int poolSize = threadPoolExecutor.getPoolSize();
        // 活跃线程数 (有锁)
        int activeCount = threadPoolExecutor.getActiveCount();
        // 同时进入池中的最大线程数 (有锁)
        int largestPoolSize = threadPoolExecutor.getLargestPoolSize();
        // 线程池中执行任务总数量 (有锁)
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        request.setActive(active.toUpperCase());
        request.setIdentify(IdentifyUtil.getIdentify());
        request.setCorePoolSize(corePoolSize);
        request.setMaximumPoolSize(maximumPoolSize);
        request.setPoolSize(poolSize);
        request.setActiveCount(activeCount);
        request.setLargestPoolSize(largestPoolSize);
        request.setCompletedTaskCount(completedTaskCount);
        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
        // 队列元素个数
        int queueSize = queue.size();
        // 队列类型
        String queueType = queue.getClass().getSimpleName();
        // 队列剩余容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列容量
        int queueCapacity = queueSize + remainingCapacity;
        request.setQueueName(queueType);
        request.setCapacity(queueCapacity);
        request.setQueueSize(queueSize);
        request.setRemainingCapacity(remainingCapacity);
        RejectedExecutionHandler rejectedExecutionHandler = threadPoolExecutor instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) threadPoolExecutor).getRedundancyHandler()
                : threadPoolExecutor.getRejectedExecutionHandler();
        request.setRejectedExecutionHandlerName(rejectedExecutionHandler.getClass().getSimpleName());
        long rejectCount = threadPoolExecutor instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) threadPoolExecutor).getRejectCountNum()
                : -1L;
        request.setRejectCountNum(rejectCount);
        return request;
    }
}
