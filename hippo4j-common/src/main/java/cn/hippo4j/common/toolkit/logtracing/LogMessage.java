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

package cn.hippo4j.common.toolkit.logtracing;

import cn.hippo4j.common.toolkit.StringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Log message.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMessage {

    private Map<String, Object> kvs = new ConcurrentHashMap<>();

    private String msg = "";

    public static LogMessage getInstance() {
        return new LogMessage();
    }

    public LogMessage setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String msg(String msg, Object... args) {
        LogMessage l = new LogMessage();
        l.kvs = this.kvs;
        return l.setMsgString(msg, args);
    }

    public LogMessage setMsg(String msg, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
        this.msg = ft.getThrowable() == null ? ft.getMessage() : ft.getMessage() + "||_fmt_throw=" + ft.getThrowable();
        return this;
    }

    public String setMsgString(String msg, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
        this.msg = ft.getThrowable() == null ? ft.getMessage() : ft.getMessage() + "||_fmt_throw=" + ft.getThrowable();
        return toString();
    }

    public LogMessage kv(String k, Object v) {
        this.kvs.put(k, v == null ? "" : v);
        return this;
    }

    public String kv2String(String k, Object v) {
        this.kvs.put(k, v == null ? "" : v);
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtil.isNotEmpty(msg)) {
            sb.append(msg);
        }
        int tempCount = 0;
        for (Map.Entry<String, Object> kv : kvs.entrySet()) {
            tempCount++;
            Object value = kv.getValue();
            if (value != null) {
                if (value instanceof String && StringUtil.isEmpty((String) value)) {
                    continue;
                }
                sb.append(kv.getKey() + "=").append(kv.getValue());
                if (tempCount != kvs.size()) {
                    sb.append("||");
                }
            }
        }
        return sb.toString();
    }
}
