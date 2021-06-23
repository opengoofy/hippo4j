package io.dynamic.threadpool.starter.http;

import java.util.Map;

/**
 * Http Agent.
 *
 * @author chen.ma
 * @date 2021/6/23 20:45
 */
public interface HttpAgent {

    /**
     * 开始获取 NacosIp 集合
     */
    void start();

    /**
     * 获取命名空间
     *
     * @return
     */
    String getNameSpace();

    /**
     * 获取编码集
     *
     * @return
     */
    String getEncode();

    /**
     * 发起 Http Get 请求
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param encoding
     * @param readTimeoutMs
     * @return
     */
    String httpGet(String path, Map<String, String> headers, Map<String, String> paramValues,
                   String encoding, long readTimeoutMs);

    /**
     * 发起 Http Post 请求
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param encoding
     * @param readTimeoutMs
     * @return
     */
    String httpPost(String path, Map<String, String> headers, Map<String, String> paramValues,
                    String encoding, long readTimeoutMs);

    /**
     * 发起 Http Delete 请求
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param encoding
     * @param readTimeoutMs
     * @return
     */
    String httpDelete(String path, Map<String, String> headers, Map<String, String> paramValues,
                      String encoding, long readTimeoutMs);
}
