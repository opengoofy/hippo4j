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

package cn.hippo4j.config.toolkit;

import cn.hippo4j.common.toolkit.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Map util.
 */
public class MapUtil {

    public static Object computeIfAbsent(Map target, Object key, BiFunction mappingFunction, Object param1, Object param2) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(mappingFunction, "mappingFunction");
        Objects.requireNonNull(param1, "param1");
        Objects.requireNonNull(param2, "param2");
        Object val = target.get(key);
        if (val == null) {
            Object ret = mappingFunction.apply(param1, param2);
            target.put(key, ret);
            return ret;
        }
        return val;
    }

    /**
     * Fuzzy matching based on Key.
     *
     * @param sourceMap
     * @param filters
     * @return
     */
    public static List<String> parseMapForFilter(Map<String, ?> sourceMap, String filters) {
        List<String> resultList = new ArrayList<>();
        if (CollectionUtil.isEmpty(sourceMap)) {
            return resultList;
        }
        sourceMap.forEach((key, val) -> {
            if (checkKey(key, filters)) {
                resultList.add(key);
            }
        });
        return resultList;
    }

    /**
     * Match the characters you want to query.
     *
     * @param key
     * @param filters
     * @return
     */
    private static boolean checkKey(String key, String filters) {
        if (key.indexOf(filters) > -1) {
            return true;
        } else {
            return false;
        }
    }
}
