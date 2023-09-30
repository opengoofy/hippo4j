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

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton repository.
 */
public class SingletonRepository<T> {

    /**
     * initialCapacity: 1 << 16
     */
    private static final int INITIAL_CAPACITY = 65536;

    public SingletonRepository() {
        shared = new ConcurrentHashMap(INITIAL_CAPACITY);
    }

    public T getSingleton(T obj) {
        T previous = shared.putIfAbsent(obj, obj);
        return (null == previous) ? obj : previous;
    }

    public int size() {
        return shared.size();
    }

    public void remove(Object obj) {
        shared.remove(obj);
    }

    private final ConcurrentHashMap<T, T> shared;

    /**
     * Data Id Group Id Cache
     */
    public static class DataIdGroupIdCache {

        public static String getSingleton(String str) {
            return cache.getSingleton(str);
        }

        static SingletonRepository<String> cache = new SingletonRepository<String>();
    }
}
