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
import cn.hippo4j.common.constant.HttpHeaderConstants;
import cn.hippo4j.common.toolkit.IoUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Represents a client-side HTTP response with JDK implementation
 */
public class JdkHttpClientResponse implements HttpClientResponse {

    private final HttpURLConnection conn;

    private InputStream responseStream;

    private Header responseHeader;

    private static final String CONTENT_ENCODING = "gzip";

    public JdkHttpClientResponse(HttpURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public Header getHeaders() {
        if (this.responseHeader == null) {
            this.responseHeader = Header.newInstance();
        }
        for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            this.responseHeader.addOriginalResponseHeader(entry.getKey(), entry.getValue());
        }
        return this.responseHeader;
    }

    @Override
    @SneakyThrows
    public InputStream getBody() {
        Header headers = getHeaders();
        InputStream errorStream = this.conn.getErrorStream();
        this.responseStream = (errorStream != null ? errorStream : this.conn.getInputStream());
        String contentEncoding = headers.getValue(HttpHeaderConstants.CONTENT_ENCODING);
        // Used to process http content_encoding, when content_encoding is GZIP, use GZIPInputStream
        if (CONTENT_ENCODING.equals(contentEncoding)) {
            byte[] bytes = IoUtil.tryDecompress(this.responseStream);
            return new ByteArrayInputStream(bytes);
        }
        return this.responseStream;
    }

    @Override
    @SneakyThrows
    public int getStatusCode() {
        return this.conn.getResponseCode();
    }

    @Override
    @SneakyThrows
    public String getStatusText() {
        return this.conn.getResponseMessage();
    }

    @Override
    public String getBodyString() {
        return IoUtil.toString(this.getBody(), Constants.ENCODE);
    }

    @Override
    public void close() {
        IoUtil.closeQuietly(this.responseStream);
    }
}
