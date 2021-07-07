package io.dynamic.threadpool.starter.remote;

import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.starter.config.DynamicThreadPoolProperties;
import io.dynamic.threadpool.starter.toolkit.HttpClientUtil;
import io.dynamic.threadpool.common.web.base.Result;

import java.util.Map;

/**
 * Server Http Agent.
 *
 * @author chen.ma
 * @date 2021/6/23 20:50
 */
public class ServerHttpAgent implements HttpAgent {

    private final DynamicThreadPoolProperties dynamicThreadPoolProperties;

    private final ServerListManager serverListManager;

    private HttpClientUtil httpClientUtil = ApplicationContextHolder.getBean(HttpClientUtil.class);

    public ServerHttpAgent(DynamicThreadPoolProperties properties) {
        this.dynamicThreadPoolProperties = properties;
        this.serverListManager = new ServerListManager(dynamicThreadPoolProperties);
    }

    @Override
    public void start() {

    }

    @Override
    public Result httpGet(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        return httpClientUtil.restApiGetByThreadPool(buildUrl(path), headers, paramValues, readTimeoutMs, Result.class);
    }

    @Override
    public Result httpPost(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        return httpClientUtil.restApiPostByThreadPool(buildUrl(path), headers, paramValues, readTimeoutMs, Result.class);
    }

    @Override
    public Result httpDelete(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        return null;
    }

    @Override
    public String getTenantId() {
        return dynamicThreadPoolProperties.getNamespace();
    }

    @Override
    public String getEncode() {
        return null;
    }

    private String buildUrl(String path) {
        return serverListManager.getCurrentServerAddr() + path;
    }
}
