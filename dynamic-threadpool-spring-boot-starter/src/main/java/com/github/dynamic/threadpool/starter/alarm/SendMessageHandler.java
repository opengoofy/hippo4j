package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;

import java.util.List;

/**
 * Send message handler.
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
     * @param notifyConfigs
     * @param threadPoolExecutor
     */
    void sendAlarmMessage(List<NotifyConfig> notifyConfigs, CustomThreadPoolExecutor threadPoolExecutor);

    /**
     * Send change message.
     *
     * @param notifyConfigs
     * @param parameter
     */
    void sendChangeMessage(List<NotifyConfig> notifyConfigs, PoolParameterInfo parameter);

}
