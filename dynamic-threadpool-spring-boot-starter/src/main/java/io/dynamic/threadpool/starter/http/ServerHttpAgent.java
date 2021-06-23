package io.dynamic.threadpool.starter.http;

import io.dynamic.threadpool.starter.config.DynamicThreadPoolProperties;

import java.util.Map;

/**
 * Server Http Agent.
 *
 * @author chen.ma
 * @date 2021/6/23 20:50
 */
public class ServerHttpAgent implements HttpAgent {

    private final DynamicThreadPoolProperties dynamicThreadPoolProperties;

    public ServerHttpAgent(DynamicThreadPoolProperties properties) {
        this.dynamicThreadPoolProperties = properties;
    }

    @Override
    public void start() {

    }

    @Override
    public String httpGet(String path, Map<String, String> headers, Map<String, String> paramValues, String encoding, long readTimeoutMs) {
        return null;
    }

    @Override
    public String httpPost(String path, Map<String, String> headers, Map<String, String> paramValues, String encoding, long readTimeoutMs) {
        return null;
    }

    @Override
    public String httpDelete(String path, Map<String, String> headers, Map<String, String> paramValues, String encoding, long readTimeoutMs) {
        return null;
    }

    @Override
    public String getNameSpace() {
        return null;
    }

    @Override
    public String getEncode() {
        return null;
    }
}
