package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;

/**
 * Send msg.
 *
 * @author chen.ma
 * @date 2021/8/15 15:31
 */
public interface SendMessageService {

    /**
     * Send alarm message.
     *
     * @param threadPoolExecutor
     */
    void sendAlarmMessage(CustomThreadPoolExecutor threadPoolExecutor);

    /**
     * Send change message.
     *
     * @param parameter
     */
    void sendChangeMessage(PoolParameterInfo parameter);

}
