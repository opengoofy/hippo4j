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
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.IoUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.logtracing.LogMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

import static cn.hippo4j.common.constant.HttpHeaderConstants.CONTENT_LENGTH;

/**
 * Http request utilities.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtil {

    /**
     * Default connect timeout
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;

    /**
     * Default read timeout
     */
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    /**
     * Send a get network request.
     *
     * @param url     target url
     * @param headers headers
     * @param params  form data
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T get(String url, Map<String, String> headers, Map<String, String> params, long timeout, Class<T> clazz) {
        return execute(buildUrl(url, params), HttpMethod.GET, null, headers, timeout, clazz);
    }

    /**
     * Send a get network request.
     *
     * @param url    target url
     * @param params form data
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return execute(buildUrl(url, params), HttpMethod.GET, null, null);
    }

    /**
     * Send a get network request.
     *
     * @param url target url
     * @return
     */
    public static String get(String url) {
        return execute(url, HttpMethod.GET, null, null);
    }

    /**
     * Send a get network request.
     *
     * @param url   target url
     * @param clazz return the target data type
     * @param <T>   return the target data type
     * @return
     */
    public static <T> T get(String url, Class<T> clazz) {
        return JSONUtil.parseObject(get(url), clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url   target url
     * @param body  request body
     * @param clazz return the target data type
     * @param <T>   return the target data type
     * @return
     */
    public static <T> T post(String url, Object body, Class<T> clazz) {
        String result = post(url, body);
        return JSONUtil.parseObject(result, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param body    request body
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T post(String url, Object body, long timeout, Class<T> clazz) {
        String result = post(url, body, timeout);
        return JSONUtil.parseObject(result, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param headers headers
     * @param params  form data
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T post(String url, Map<String, String> headers, Map<String, String> params, long timeout, Class<T> clazz) {
        return execute(buildUrl(url, params), HttpMethod.POST, null, headers, timeout, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param headers headers
     * @param body    request body
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T post(String url, Map<String, String> headers, Object body, long timeout, Class<T> clazz) {
        return execute(url, HttpMethod.POST, body, headers, timeout, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url  target url
     * @param body request body
     * @return
     */
    public static String post(String url, Object body) {
        return execute(url, HttpMethod.POST, body, null);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param body    request body
     * @param timeout request timeout
     * @return
     */
    public static String post(String url, Object body, long timeout) {
        return execute(url, HttpMethod.POST, body, null, timeout, String.class);
    }

    /**
     * Send a post network request.
     *
     * @param url  target url
     * @param json json data
     * @return
     */
    public static String postJson(String url, String json) {
        return executeJson(url, HttpMethod.POST, json, null);
    }

    /**
     * Send a put network request.
     *
     * @param url  target url
     * @param body request body
     * @return
     */
    public static String put(String url, Object body) {
        return execute(url, HttpMethod.PUT, body, null);
    }

    /**
     * Send a put network request.
     *
     * @param url     target url
     * @param body    request body
     * @param headers headers
     * @return
     */
    public static String put(String url, Object body, Map<String, String> headers) {
        return execute(url, HttpMethod.PUT, body, headers);
    }

    /**
     * Constructs a complete Url from the query string.
     *
     * @param url         target url
     * @param queryParams query params
     * @return
     */
    @SneakyThrows
    public static String buildUrl(String url, Map<String, String> queryParams) {
        if (CollectionUtil.isEmpty(queryParams)) {
            return url;
        }
        boolean isFirst = true;
        StringBuilder builder = new StringBuilder(url);
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            if (key != null && entry.getValue() != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                String value = URLEncoder.encode(queryParams.get(key), Constants.ENCODE)
                        .replaceAll("\\+", "%20");
                builder.append(key)
                        .append("=")
                        .append(value);
            }
        }
        return builder.toString();
    }

    private static String executeJson(String url, String method, String json, Map<String, String> headers) {
        if (!JSONUtil.isJson(json)) {
            log.error(LogMessage.getInstance().setMsg("Http Call error.")
                    .kv("url", url)
                    .kv("method", method)
                    .kv("json", json)
                    .kv2String("headers", JSONUtil.toJSONString(headers)));
            throw new RuntimeException("Invalid http json body, please check it again.");
        }
        return execute(url, method, json, headers);
    }

    private static String execute(String url, String method, Object param, Map<String, String> headers) {
        HttpURLConnection connection = createConnection(url, method);
        HttpClientResponse response = null;
        try {
            response = doExecute(connection, param, headers);
            return response.getBodyString();
        } finally {
            Optional.ofNullable(response).ifPresent(each -> each.close());
        }
    }

    private static <T> T execute(String url, String method, Object body, Map<String, String> headers, long timeout, Class<T> clazz) {
        HttpURLConnection connection = createConnection(url, method, timeout);
        HttpClientResponse response = null;
        try {
            response = doExecute(connection, body, headers);
            if (clazz == String.class) {
                return (T) response.getBodyString();
            }
            return JSONUtil.parseObject(response.getBodyString(), clazz);
        } finally {
            Optional.ofNullable(response).ifPresent(each -> each.close());
        }
    }

    @SneakyThrows
    private static HttpClientResponse doExecute(HttpURLConnection connection, Object body, Map<String, String> headers) {
        try {
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
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
                IoUtil.closeQuietly(outputStream);
            }
            connection.connect();
            JdkHttpClientResponse response = new JdkHttpClientResponse(connection);
            if (!HttpResponseCode.isOk(response.getStatusCode())) {
                String msg = String.format("HttpPost response code error. [code] %s [url] %s [body] %s", response.getStatusCode(), connection.getURL(), response.getBodyString());
                throw new RuntimeException(msg);
            }
            return response;
        } catch (Throwable ex) {
            log.error(LogMessage.getInstance().setMsg("Http call error. ")
                    .kv("url", connection.getURL())
                    .kv("method", connection.getRequestMethod())
                    .kv("body", JSONUtil.toJSONString(body))
                    .kv2String("headers", JSONUtil.toJSONString(headers)), ex);
            throw ex;
        }
    }

    @SneakyThrows
    private static HttpURLConnection createConnection(String url, String method) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setRequestMethod(method);
        connection.setRequestProperty(Constants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        return connection;
    }

    @SneakyThrows
    private static HttpURLConnection createConnection(String url, String method, long timeout) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(Integer.parseInt(String.valueOf(timeout)));
        connection.setReadTimeout(Integer.parseInt(String.valueOf(timeout)));
        connection.setRequestMethod(method);
        connection.setRequestProperty(Constants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        return connection;
    }
}
