package com.github.dynamic.threadpool.starter.alarm;

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
     * getType.
     *
     * @return
     */
    String getType();

    /**
     * sendMessage.
     *
     * @param alarmConfigs
     * @param threadPoolExecutor
     */
    void sendMessage(List<AlarmConfig> alarmConfigs, CustomThreadPoolExecutor threadPoolExecutor);

}
