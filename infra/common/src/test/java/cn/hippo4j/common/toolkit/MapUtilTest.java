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

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * MapUtil Test
 */
public class MapUtilTest {

    @Test
    public void parseMapForFilterRetIsEmptyTest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        map.put("bcd", "456");
        map.put("cde", "789");
        List<String> ret = MapUtil.parseMapForFilter(map, "x");
        Assert.isTrue(CollectionUtil.isEmpty(ret));
    }

    @Test
    public void parseMapForFilterRetIsNotEmptyTest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        map.put("bcd", "456");
        map.put("cde", "789");
        List<String> ret = MapUtil.parseMapForFilter(map, "b");
        Assert.isTrue(CollectionUtil.isNotEmpty(ret));
    }

    @Test
    public void computeIfAbsentNotExistTargetTest() {
        BiFunction<String, String, String> mappingFunction = (a, b) -> a + b;
        try {
            MapUtil.computeIfAbsent(null, "key", mappingFunction, "param1", "param2");
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                Assert.isTrue(Objects.equals("target", e.getMessage()));
            }
        }
    }

    @Test
    public void computeIfAbsentNotExistKeyTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        BiFunction<String, String, Object> mappingFunction = (a, b) -> a + b;
        try {
            MapUtil.computeIfAbsent(map, null, mappingFunction, "param1", "param2");
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                Assert.isTrue(Objects.equals("key", e.getMessage()));
            }
        }
    }

    @Test
    public void computeIfAbsentNotExistMappingFunctionTest() {
        Map<Object, Object> map = new HashMap<>();
        map.put("abc", "123");
        try {
            MapUtil.computeIfAbsent(map, "abc", null, "param1", "param2");
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                Assert.isTrue(Objects.equals("mappingFunction", e.getMessage()));
            }
        }
    }

    @Test
    public void computeIfAbsentNotExistParam1Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        BiFunction<String, String, Object> mappingFunction = (a, b) -> a + b;
        try {
            MapUtil.computeIfAbsent(map, "abc", mappingFunction, null, "param2");
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                Assert.isTrue(Objects.equals("param1", e.getMessage()));
            }
        }
    }

    @Test
    public void computeIfAbsentNotExistParam2Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        BiFunction<String, String, Object> mappingFunction = (a, b) -> a + b;
        try {
            MapUtil.computeIfAbsent(map, "abc", mappingFunction, "param1", null);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                Assert.isTrue(Objects.equals("param2", e.getMessage()));
            }
        }
    }

    @Test
    public void computeIfAbsentValNotNullTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        BiFunction<String, String, Object> mappingFunction = (a, b) -> a + b;
        Object ret = MapUtil.computeIfAbsent(map, "abc", mappingFunction, "param1", "param2");
        Assert.isTrue(Objects.equals("123", String.valueOf(ret)));
    }

    @Test
    public void computeIfAbsentValIsNullTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("abc", "123");
        BiFunction<String, String, Object> mappingFunction = (a, b) -> a + b;
        Object ret = MapUtil.computeIfAbsent(map, "xyz", mappingFunction, "param1", "param2");
        Assert.isTrue(Objects.equals("param1param2", String.valueOf(ret)));
    }

}
