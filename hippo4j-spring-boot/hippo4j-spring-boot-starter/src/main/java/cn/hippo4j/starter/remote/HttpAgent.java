package cn.hippo4j.starter.remote;

import cn.hippo4j.common.web.base.Result;

import java.util.Map;

/**
 * Http agent.
 *
 * @author chen.ma
 * @date 2021/6/23 20:45
 */
public interface HttpAgent {

    /**
     * Start.
     */
    void start();

    /**
     * Get tenant id.
     *
     * @return
     */
    String getTenantId();

    /**
     * Get encode.
     *
     * @return
     */
    String getEncode();

    /**
     * Http get simple.
     *
     * @param path
     * @return
     */
    Result httpGetSimple(String path);

    /**
     * Http post.
     *
     * @param path
     * @param body
     * @return
     */
    Result httpPost(String path, Object body);

    /**
     * Send HTTP post request by discovery.
     *
     * @param path
     * @param body
     * @return
     */
    Result httpPostByDiscovery(String path, Object body);

    /**
     * Send HTTP get request by dynamic config.
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
     * Send HTTP post request by dynamic config.
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
     * Send HTTP delete request by dynamic config.
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
