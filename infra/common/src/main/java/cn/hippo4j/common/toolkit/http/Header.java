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
import cn.hippo4j.common.constant.HttpMediaType;
import cn.hippo4j.common.toolkit.MapUtil;
import cn.hippo4j.common.toolkit.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Http header.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class Header {

    public static final Header EMPTY = Header.newInstance();

    private final Map<String, String> header;

    private final Map<String, List<String>> originalResponseHeader;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String DEFAULT_ENCODING = "gzip";

    private Header() {
        header = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        originalResponseHeader = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        addParam(HttpHeaderConstants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        addParam(HttpHeaderConstants.ACCEPT_CHARSET, DEFAULT_CHARSET);
    }

    public static Header newInstance() {
        return new Header();
    }

    /**
     * Add the key and value to the header.
     *
     * @param key   the key
     * @param value the value
     * @return header
     */
    public Header addParam(String key, String value) {
        if (StringUtil.isNotEmpty(key)) {
            header.put(key, value);
        }
        return this;
    }

    public Header setContentType(String contentType) {
        if (contentType == null) {
            contentType = HttpMediaType.APPLICATION_JSON;
        }
        return addParam(HttpHeaderConstants.CONTENT_TYPE, contentType);
    }

    public Header build() {
        return this;
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        return header.entrySet().iterator();
    }

    /**
     * Transfer to KV part list. The odd index is key and the even index is value.
     *
     * @return KV string list
     */
    public List<String> toList() {
        List<String> list = new ArrayList<>(header.size() * 2);
        Iterator<Map.Entry<String, String>> iterator = iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }

    /**
     * Add all KV list to header. The odd index is key and the even index is value.
     *
     * @param list KV list
     * @return header
     */
    public Header addAll(List<String> list) {
        if ((list.size() & 1) != 0) {
            throw new IllegalArgumentException("list size must be a multiple of 2");
        }
        for (int i = 0; i < list.size();) {
            String key = list.get(i++);
            if (StringUtil.isNotEmpty(key)) {
                header.put(key, list.get(i++));
            }
        }
        return this;
    }

    /**
     * Add all parameters to header.
     *
     * @param params parameters
     */
    public void addAll(Map<String, String> params) {
        if (MapUtil.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                addParam(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * set original format response header.
     *
     * <p>Currently only corresponds to the response header of JDK.
     *
     * @param key    original response header key
     * @param values original response header values
     */
    public void addOriginalResponseHeader(String key, List<String> values) {
        if (StringUtil.isNotEmpty(key)) {
            this.originalResponseHeader.put(key, values);
            addParam(key, values.get(0));
        }
    }

    /**
     * get original format response header.
     *
     * <p>Currently only corresponds to the response header of JDK.
     *
     * @return Map original response header
     */
    public Map<String, List<String>> getOriginalResponseHeader() {
        return this.originalResponseHeader;
    }

    public String getCharset() {
        String acceptCharset = getValue(HttpHeaderConstants.ACCEPT_CHARSET);
        if (acceptCharset == null) {
            String contentType = getValue(HttpHeaderConstants.CONTENT_TYPE);
            acceptCharset = StringUtil.isNotBlank(contentType) ? analysisCharset(contentType) : Constants.ENCODE;
        }
        return acceptCharset;
    }

    private String analysisCharset(String contentType) {
        String[] values = contentType.split(";");
        String charset = Constants.ENCODE;
        if (values.length == 0) {
            return charset;
        }
        for (String value : values) {
            if (value.startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }
        return charset;
    }

    public void clear() {
        header.clear();
        originalResponseHeader.clear();
    }

    @Override
    public String toString() {
        return "Header{" + "headerToMap=" + header + '}';
    }
}
