package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.config.MessageAlarmConfig;
import com.github.dynamic.threadpool.starter.toolkit.CalculateUtil;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import com.github.dynamic.threadpool.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Alarm manage.
 *
 * @author chen.ma
 * @date 2021/8/15 14:13
 */
@Slf4j
public class ThreadPoolAlarmManage {

    /**
     * 发送消息
     */
    private static final SendMessageService SEND_MESSAGE_SERVICE;

    /**
     * 报警间隔控制
     */
    private static final AlarmControlHandler ALARM_CONTROL_HANDLER;

    static {
        SEND_MESSAGE_SERVICE = Optional.ofNullable(ApplicationContextHolder.getInstance())
                .map(each -> each.getBean(MessageAlarmConfig.SEND_MESSAGE_BEAN_NAME, SendMessageService.class))
                .orElse(null);

        ALARM_CONTROL_HANDLER = Optional.ofNullable(ApplicationContextHolder.getInstance())
                .map(each -> each.getBean(AlarmControlHandler.class))
                .orElse(null);
    }

    /**
     * Check thread pool capacity alarm.
     *
     * @param threadPoolExecutor
     */
    public static void checkPoolCapacityAlarm(CustomThreadPoolExecutor threadPoolExecutor) {
        if (SEND_MESSAGE_SERVICE == null) {
            return;
        }
        ThreadPoolAlarm threadPoolAlarm = threadPoolExecutor.getThreadPoolAlarm();
        ResizableCapacityLinkedBlockIngQueue blockIngQueue =
                (ResizableCapacityLinkedBlockIngQueue) threadPoolExecutor.getQueue();

        int queueSize = blockIngQueue.size();
        int capacity = queueSize + blockIngQueue.remainingCapacity();
        int divide = CalculateUtil.divide(queueSize, capacity);
        boolean isSend = divide > threadPoolAlarm.getCapacityAlarm()
                && isSendMessage(threadPoolExecutor, MessageTypeEnum.CAPACITY);
        if (isSend) {
            SEND_MESSAGE_SERVICE.sendAlarmMessage(threadPoolExecutor);
        }
    }

    /**
     * Check thread pool activity alarm.
     *
     * @param isCore
     * @param threadPoolExecutor
     */
    public static void checkPoolLivenessAlarm(boolean isCore, CustomThreadPoolExecutor threadPoolExecutor) {
        if (isCore || SEND_MESSAGE_SERVICE == null || !isSendMessage(threadPoolExecutor, MessageTypeEnum.LIVENESS)) {
            return;
        }
        int activeCount = threadPoolExecutor.getActiveCount();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int divide = CalculateUtil.divide(activeCount, maximumPoolSize);

        boolean isSend = divide > threadPoolExecutor.getThreadPoolAlarm().getLivenessAlarm()
                && isSendMessage(threadPoolExecutor, MessageTypeEnum.CAPACITY);
        if (isSend) {
            SEND_MESSAGE_SERVICE.sendAlarmMessage(threadPoolExecutor);
        }
    }

    /**
     * Check thread pool reject policy alarm.
     *
     * @param threadPoolExecutor
     */
    public static void checkPoolRejectAlarm(CustomThreadPoolExecutor threadPoolExecutor) {
        if (SEND_MESSAGE_SERVICE == null) {
            return;
        }

        if (isSendMessage(threadPoolExecutor, MessageTypeEnum.REJECT)) {
            SEND_MESSAGE_SERVICE.sendAlarmMessage(threadPoolExecutor);
        }
    }

    /**
     * Send thread pool configuration change message.
     *
     * @param parameter
     */
    public static void sendPoolConfigChange(PoolParameterInfo parameter) {
        if (SEND_MESSAGE_SERVICE == null) {
            return;
        }

        SEND_MESSAGE_SERVICE.sendChangeMessage(parameter);
    }

    /**
     * Is send message.
     *
     * @param threadPoolExecutor
     * @param typeEnum
     * @return
     */
    private static boolean isSendMessage(CustomThreadPoolExecutor threadPoolExecutor, MessageTypeEnum typeEnum) {
        AlarmControlDTO alarmControl = AlarmControlDTO.builder()
                .threadPool(threadPoolExecutor.getThreadPoolId())
                .typeEnum(typeEnum)
                .build();
        return ALARM_CONTROL_HANDLER.isSend(alarmControl);
    }

}
