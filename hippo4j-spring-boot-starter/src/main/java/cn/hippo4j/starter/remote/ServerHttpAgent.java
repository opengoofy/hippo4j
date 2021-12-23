package cn.hippo4j.starter.remote;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.security.SecurityProxy;
import cn.hippo4j.starter.toolkit.HttpClientUtil;
import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Server http agent.
 *
 * @author chen.ma
 * @date 2021/6/23 20:50
 */
public class ServerHttpAgent implements HttpAgent {

    private final BootstrapProperties dynamicThreadPoolProperties;

    private final ServerListManager serverListManager;

    private final HttpClientUtil httpClientUtil;

    private SecurityProxy securityProxy;

    private ServerHealthCheck serverHealthCheck;

    private ScheduledExecutorService executorService;

    private final long securityInfoRefreshIntervalMills = TimeUnit.SECONDS.toMillis(5);

    public ServerHttpAgent(BootstrapProperties properties, HttpClientUtil httpClientUtil) {
        this.dynamicThreadPoolProperties = properties;
        this.httpClientUtil = httpClientUtil;
        this.serverListManager = new ServerListManager(dynamicThreadPoolProperties);
        this.securityProxy = new SecurityProxy(httpClientUtil, properties);

        this.securityProxy.applyToken(this.serverListManager.getServerUrls());
        this.executorService = new ScheduledThreadPoolExecutor(
                new Integer(1),
                ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.token.security.updater").build()
        );

        this.executorService.scheduleWithFixedDelay(
                () -> securityProxy.applyToken(serverListManager.getServerUrls()),
                0,
                securityInfoRefreshIntervalMills,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void start() {

    }

    @Override
    public String getTenantId() {
        return dynamicThreadPoolProperties.getNamespace();
    }

    @Override
    public String getEncode() {
        return null;
    }

    @Override
    public Result httpGetSimple(String path) {
        path = injectSecurityInfoByPath(path);
        return httpClientUtil.restApiGetHealth(buildUrl(path), Result.class);
    }

    @Override
    public Result httpPost(String path, Object body) {
        isHealthStatus();
        path = injectSecurityInfoByPath(path);
        return httpClientUtil.restApiPost(buildUrl(path), body, Result.class);
    }

    @Override
    public Result httpPostByDiscovery(String path, Object body) {
        isHealthStatus();
        path = injectSecurityInfoByPath(path);
        return httpClientUtil.restApiPost(buildUrl(path), body, Result.class);
    }

    @Override
    public Result httpGetByConfig(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        isHealthStatus();
        injectSecurityInfo(paramValues);
        return httpClientUtil.restApiGetByThreadPool(buildUrl(path), headers, paramValues, readTimeoutMs, Result.class);
    }

    @Override
    public Result httpPostByConfig(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        isHealthStatus();
        injectSecurityInfo(paramValues);
        return httpClientUtil.restApiPostByThreadPool(buildUrl(path), headers, paramValues, readTimeoutMs, Result.class);
    }

    @Override
    public Result httpDeleteByConfig(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        return null;
    }

    private String buildUrl(String path) {
        return serverListManager.getCurrentServerAddr() + path;
    }

    private void isHealthStatus() {
        if (serverHealthCheck == null) {
            serverHealthCheck = ApplicationContextHolder.getBean(ServerHealthCheck.class);
        }

        serverHealthCheck.isHealthStatus();
    }

    private Map injectSecurityInfo(Map<String, String> params) {
        if (StrUtil.isNotBlank(securityProxy.getAccessToken())) {
            params.put(Constants.ACCESS_TOKEN, securityProxy.getAccessToken());
        }

        return params;
    }

    @Deprecated
    private String injectSecurityInfoByPath(String path) {
        String resultPath = httpClientUtil.buildUrl(path, injectSecurityInfo(Maps.newHashMap()));
        return resultPath;
    }

}
