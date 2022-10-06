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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Singleton object container.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Singleton {

    private static final ConcurrentHashMap<String, Object> SINGLE_OBJECT_POOL = new ConcurrentHashMap<>();

    /**
     * Get a singleton object by key.
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(String key) {
        Object result = SINGLE_OBJECT_POOL.get(key);
        return result == null ? null : (T) result;
    }

    /**
     * Get a singleton object by key.
     *
     * <p> When empty, build a singleton object through supplier and put it into the container.
     *
     * @param key
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> T get(String key, Supplier<T> supplier) {
        Object result = SINGLE_OBJECT_POOL.get(key);
        if (result == null) {
            result = supplier.get();
            if (result != null) {
                SINGLE_OBJECT_POOL.put(key, result);
            }
        }
        return result != null ? (T) result : null;
    }

    /**
     * Object into container.
     *
     * @param value
     */
    public static void put(Object value) {
        put(value.getClass().getName(), value);
    }

    /**
     * Object into container.
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        SINGLE_OBJECT_POOL.put(key, value);
    }
}
