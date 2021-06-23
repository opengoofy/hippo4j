package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.starter.listener.Listener;

/**
 * 配置服务
 *
 * @author chen.ma
 * @date 2021/6/21 21:49
 */
public interface ConfigService {

    /**
     * 添加监听器, 如果服务端发生变更, 客户端会使用监听器进行回调
     *
     * @param namespace
     * @param itemId
     * @param tpId
     * @param listener
     */
    void addListener(String namespace, String itemId, String tpId, Listener listener);

    /**
     * 获取服务状态
     *
     * @return
     */
    String getServerStatus();
}
