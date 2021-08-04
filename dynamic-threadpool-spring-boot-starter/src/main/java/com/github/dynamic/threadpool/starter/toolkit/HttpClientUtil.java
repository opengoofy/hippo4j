package com.github.dynamic.threadpool.starter.toolkit;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Http 客户端工具类
 *
 * @author chen.ma
 * @date 2021/6/10 13:30
 */
@Slf4j
public class HttpClientUtil {

    @Autowired
    private OkHttpClient okHttpClient;

    private MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

    private static int HTTP_OK_CODE = 200;

    /**
     * Get 请求
     *
     * @param url
     * @return
     */
    @SneakyThrows
    public String get(String url) {
        try {
            return new String(doGet(url), "utf-8");
        } catch (Exception e) {
            log.error("httpGet 调用失败. {}", url, e);
            throw e;
        }
    }

    /**
     * Get 请求, 支持添加查询字符串
     *
     * @param url
     * @param queryString 查询字符串
     * @return
     */
    public String get(String url, Map<String, String> queryString) {
        String fullUrl = buildUrl(url, queryString);
        return get(fullUrl);
    }

    /**
     * 获取 Json 后直接反序列化
     *
     * @param url
     * @param clazz
     * @return
     */
    public <T> T restApiGet(String url, Class<T> clazz) {
        String resp = get(url);
        return JSON.parseObject(resp, clazz);
    }

    /**
     * Get 请求, 支持查询字符串
     *
     * @param url
     * @param queryString
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T restApiGet(String url, Map<String, String> queryString, Class<T> clazz) {
        String fullUrl = buildUrl(url, queryString);
        String resp = get(fullUrl);
        return JSON.parseObject(resp, clazz);
    }

    /**
     * Rest 接口 Post 调用
     *
     * @param url
     * @param body
     * @return
     */
    public String restApiPost(String url, Object body) {
        try {
            return doPost(url, body);
        } catch (Exception e) {
            log.error("httpPost 调用失败. {}", url, e);
            throw e;
        }
    }

    /**
     * Rest 接口 Post 调用
     * 对返回值直接反序列化
     *
     * @param url
     * @param body
     * @return
     */
    public <T> T restApiPost(String url, Object body, Class<T> clazz) {
        String resp = restApiPost(url, body);
        return JSON.parseObject(resp, clazz);
    }

    /**
     * 根据查询字符串构造完整的 Url
     *
     * @param url
     * @param queryString
     * @return
     */
    public String buildUrl(String url, Map<String, String> queryString) {
        if (null == queryString) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;

        for (Map.Entry<String, String> entry : queryString.entrySet()) {
            String key = entry.getKey();
            if (key != null && entry.getValue() != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(queryString.get(key));
            }
        }

        return builder.toString();
    }

    @SneakyThrows
    private String doPost(String url, Object body) {
        String jsonBody = JSON.toJSONString(body);
        RequestBody requestBody = RequestBody.create(jsonMediaType, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response resp = okHttpClient.newCall(request).execute();
        if (resp.code() != HTTP_OK_CODE) {
            String msg = String.format("HttpPost 响应 code 异常. [code] %s [url] %s [body] %s", resp.code(), url, jsonBody);
            throw new RuntimeException(msg);
        }
        return resp.body().string();
    }

    @SneakyThrows
    private byte[] doGet(String url) {
        Request request = new Request.Builder().get().url(url).build();
        Response resp = okHttpClient.newCall(request).execute();
        if (resp.code() != HTTP_OK_CODE) {
            String msg = String.format("HttpGet 响应 code 异常. [code] %s [url] %s", resp.code(), url);
            throw new RuntimeException(msg);
        }
        return resp.body().bytes();
    }

    @SneakyThrows
    public <T> T restApiGetByThreadPool(String url, Map<String, String> headers, Map<String, String> paramValues, Long readTimeoutMs, Class<T> clazz) {
        String buildUrl = buildUrl(url, paramValues);
        Request.Builder builder = new Request.Builder().get();

        if (!CollectionUtils.isEmpty(headers)) {
            builder.headers(Headers.of(headers));
        }

        Request request = builder.url(buildUrl).build();

        Call call = okHttpClient.newCall(request);
        call.timeout().timeout(readTimeoutMs, TimeUnit.MILLISECONDS);

        Response resp = call.execute();
        if (resp.code() != HTTP_OK_CODE) {
            String msg = String.format("HttpGet 响应 code 异常. [code] %s [url] %s", resp.code(), url);
            log.error(msg);
            throw new RuntimeException(msg);
        }

        return JSON.parseObject(resp.body().string(), clazz);
    }

    @SneakyThrows
    public <T> T restApiPostByThreadPool(String url, Map<String, String> headers, Map<String, String> paramValues, Long readTimeoutMs, Class<T> clazz) {
        String buildUrl = buildUrl(url, paramValues);

        Request request = new Request.Builder()
                .url(buildUrl)
                .headers(Headers.of(headers))
                .post(RequestBody.create(jsonMediaType, ""))
                .build();

        Call call = okHttpClient.newCall(request);
        call.timeout().timeout(readTimeoutMs, TimeUnit.MILLISECONDS);

        Response resp = call.execute();
        if (resp.code() != HTTP_OK_CODE) {
            String msg = String.format("HttpPost 响应 code 异常. [code] %s [url] %s.", resp.code(), url);
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return JSON.parseObject(resp.body().string(), clazz);
    }

}
