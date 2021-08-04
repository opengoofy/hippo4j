package com.github.dynamic.threadpool.starter.core;

/**
 * ThreadPoolSubscribeCallback.
 *
 * @author chen.ma
 * @date 2021/6/22 20:26
 */
public interface ThreadPoolSubscribeCallback {

    /**
     * 回调函数
     *
     * @param config
     */
    void callback(String config);
}
