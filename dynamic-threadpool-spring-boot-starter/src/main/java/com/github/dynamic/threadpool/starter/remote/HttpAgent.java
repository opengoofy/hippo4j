package com.github.dynamic.threadpool.starter.remote;

import com.github.dynamic.threadpool.common.web.base.Result;

import java.util.Map;

/**
 * Http Agent.
 *
 * @author chen.ma
 * @date 2021/6/23 20:45
 */
public interface HttpAgent {

    /**
     * 开始获取服务集合
     */
    void start();

    /**
     * 获取命名空间
     *
     * @return
     */
    String getTenantId();

    /**
     * 获取编码集
     *
     * @return
     */
    String getEncode();

    /**
     * 发起 Http Post 请求 By Discovery
     *
     * @param path
     * @param body
     * @return
     */
    Result httpPostByDiscovery(String path, Object body);

    /**
     * 发起 Http Get 请求 By 动态配置
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpGetByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                           long readTimeoutMs);

    /**
     * 发起 Http Post 请求 By 动态配置
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpPostByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                            long readTimeoutMs);

    /**
     * 发起 Http Delete 请求 By 动态配置
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpDeleteByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                              long readTimeoutMs);

}
