package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;

import java.util.List;

/**
 * Send Message Handler.
 *
 * @author chen.ma
 * @date 2021/8/15 15:44
 */
public interface SendMessageHandler {

    /**
     * Get type.
     *
     * @return
     */
    String getType();

    /**
     * Send alarm message.
     *
     * @param alarmConfigs
     * @param threadPoolExecutor
     */
    void sendAlarmMessage(List<AlarmConfig> alarmConfigs, CustomThreadPoolExecutor threadPoolExecutor);

    /**
     * Send change message.
     *
     * @param alarmConfigs
     * @param parameter
     */
    void sendChangeMessage(List<AlarmConfig> alarmConfigs, PoolParameterInfo parameter);

}
