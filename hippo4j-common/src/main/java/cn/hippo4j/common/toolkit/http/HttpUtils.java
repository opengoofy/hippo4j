/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.common.toolkit.http;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.constant.HttpMediaType;
import cn.hippo4j.common.constant.HttpMethod;
import cn.hippo4j.common.constant.HttpResponseCode;
import cn.hippo4j.common.toolkit.IoUtils;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.logtracing.LogMessage;
import cn.hippo4j.common.web.exception.ServiceException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import static cn.hippo4j.common.constant.HttpHeaderConsts.CONTENT_LENGTH;

/**
 * Http request utilities.
 * @author Rongzhen Yan
 */
@Slf4j
public class HttpUtils {

    private static final int CONNECT_TIMEOUT = 10000;

    private static final int READ_TIMEOUT = 300000;

    private HttpUtils() {
    }

    public static <T> T post(String url, Object body, Class<T> clazz) {
        String result = post(url, body);
        return JSONUtil.parseObject(result, clazz);
    }

    public static <T> T post(String url, Object body, long timeoutMillis, Class<T> clazz) {
        String result = post(url, body, timeoutMillis);
        return JSONUtil.parseObject(result, clazz);
    }

    public static <T> T post(String url, Map<String, String> headers, Map<String, String> params, long timeoutMillis, Class<T> clazz) {
        String result = execute(buildUrl(url, params), HttpMethod.POST, null, headers, timeoutMillis).getBodyString();
        return JSONUtil.parseObject(result, clazz);
    }

    public static <T> T post(String url, Map<String, String> headers, Object body, long timeoutMillis, Class<T> clazz) {
        String result = execute(url, HttpMethod.POST, body, headers, timeoutMillis).getBodyString();
        return JSONUtil.parseObject(result, clazz);
    }

    public static String post(String url, Object body) {
        return execute(url, HttpMethod.POST, body, null).getBodyString();
    }

    public static String post(String url, Object body, long timeoutMillis) {
        return execute(url, HttpMethod.POST, body, null, timeoutMillis).getBodyString();
    }

    public static String postJson(String url, String json) {
        return executeJson(url, HttpMethod.POST, json, null).getBodyString();
    }

    public static String put(String url, Object body) {
        return execute(url, HttpMethod.PUT, body, null).getBodyString();
    }

    public static String put(String url, Object body, Map<String, String> headers) {
        return execute(url, HttpMethod.PUT, body, headers).getBodyString();
    }

    public static <T> T get(String url, Map<String, String> headers, Map<String, String> params, long readTimeoutMillis, Class<T> clazz) {
        String result = execute(buildUrl(url, params), HttpMethod.GET, null, headers, readTimeoutMillis).getBodyString();
        return JSONUtil.parseObject(result, clazz);
    }

    public static String get(String url, Map<String, String> params) {
        return execute(buildUrl(url, params), HttpMethod.GET, null, null).getBodyString();
    }

    public static String get(String url) {
        return execute(url, HttpMethod.GET, null, null).getBodyString();
    }

    public static <T> T get(String url, Class<T> clazz) {
        return JSONUtil.parseObject(get(url), clazz);
    }

    /**
     * Constructs a complete Url from the query string.
     * @param url
     * @param queryString
     * @return
     */
    public static String buildUrl(String url, Map<String, String> queryString) {
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

    public static HttpClientResponse execute(String url, String method, Object param, Map<String, String> headers) {
        HttpURLConnection connection = createConnection(url, method);
        return doExecute(connection, param, headers);
    }

    @SneakyThrows
    public static HttpClientResponse doExecute(HttpURLConnection connection, Object body, Map<String, String> headers) {
        try {
            if (headers != null) {
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
            String bodyString;
            if (body instanceof String) {
                bodyString = (String) body;
            } else {
                bodyString = JSONUtil.toJSONString(body);
            }
            if (!StringUtil.isEmpty(bodyString)) {
                connection.setDoOutput(true);
                byte[] b = bodyString.getBytes();
                connection.setRequestProperty(CONTENT_LENGTH, String.valueOf(b.length));
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(b, 0, b.length);
                outputStream.flush();
                IoUtils.closeQuietly(outputStream);
            }
            connection.connect();
            JdkHttpClientResponse response = new JdkHttpClientResponse(connection);
            if (HttpResponseCode.SC_OK != response.getStatusCode()) {
                String msg = String.format("HttpPost response code error. [code] %s [url] %s [body] %s", response.getStatusCode(), connection.getURL(), response.getBodyString());
                throw new ServiceException(msg);
            }
            return response;
        } catch (Throwable e) {
            log.error(LogMessage.getInstance().setMsg("Http Call error.")
                    .kv("url", connection.getURL())
                    .kv("method", connection.getRequestMethod())
                    .kv("body", JSONUtil.toJSONString(body))
                    .kv2String("headers", JSONUtil.toJSONString(headers)), e);
            throw e;
        }
    }

    public static HttpClientResponse execute(String url, String method, Object body, Map<String, String> headers, long timeout) {
        HttpURLConnection connection = createConnection(url, method, timeout);
        return doExecute(connection, body, headers);
    }

    public static HttpClientResponse executeJson(String url, String method, String json, Map<String, String> headers) {
        if (!JSONUtil.isJson(json)) {
            log.error(LogMessage.getInstance().setMsg("Http Call error.")
                    .kv("url", url)
                    .kv("method", method)
                    .kv("json", json)
                    .kv2String("headers", JSONUtil.toJSONString(headers)));
            throw new ServiceException("invalid http json body, please check it again.");
        }
        return execute(url, method, json, headers);
    }

    @SneakyThrows
    private static HttpURLConnection createConnection(String url, String method) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestMethod(method);
        connection.setRequestProperty(Constants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        return connection;
    }

    @SneakyThrows
    private static HttpURLConnection createConnection(String url, String method, long timeout) {
        HttpURLConnection connection = (HttpURLConnection) new URL(URLEncoder.encode(url)).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(Integer.parseInt(String.valueOf(timeout)));
        connection.setReadTimeout(Integer.parseInt(String.valueOf(timeout)));
        connection.setRequestMethod(method);
        connection.setRequestProperty(Constants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        return connection;
    }
}
