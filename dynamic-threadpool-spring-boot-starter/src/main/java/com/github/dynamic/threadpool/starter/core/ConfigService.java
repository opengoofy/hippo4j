package com.github.dynamic.threadpool.starter.core;

/**
 * Config Service.
 *
 * @author chen.ma
 * @date 2021/6/21 21:49
 */
public interface ConfigService {

    /**
     * 添加监听器, 如果服务端发生变更, 客户端会使用监听器进行回调
     *
     * @param tenantId
     * @param itemId
     * @param tpId
     * @param listener
     */
    void addListener(String tenantId, String itemId, String tpId, Listener listener);

    /**
     * 获取服务状态
     *
     * @return
     */
    String getServerStatus();

}
