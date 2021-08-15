package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;

/**
 * Send Msg.
 *
 * @author chen.ma
 * @date 2021/8/15 15:31
 */
public interface SendMessageService {

    /**
     * sendMessage.
     *
     * @param threadPoolExecutor
     */
    void sendMessage(CustomThreadPoolExecutor threadPoolExecutor);

}
