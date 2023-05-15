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

package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.api.JsonFacade;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;

/**
 * JSON util.
 */
public class JSONUtil {

    private static final JsonFacade JSON_FACADE = new JacksonHandler();

    public static String toJSONString(Object object) {
        if (object == null) {
            return null;
        }
        return JSON_FACADE.toJSONString(object);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtil.isBlank(text)) {
            return null;
        }
        return JSON_FACADE.parseObject(text, clazz);
    }

    public static <T> T parseObject(String text, TypeReference<T> valueTypeRef) {
        if (StringUtil.isBlank(text)) {
            return null;
        }
        return JSON_FACADE.parseObject(text, valueTypeRef);
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtil.isBlank(text)) {
            return Collections.emptyList();
        }
        return JSON_FACADE.parseArray(text, clazz);
    }

    public static boolean isJson(String json) {
        if (StringUtil.isBlank(json)) {
            return false;
        }
        return JSON_FACADE.isJson(json);
    }
}
