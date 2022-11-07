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

package cn.hippo4j.rpc.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the registration center for Client and Server
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassRegistry {

    private static final Map<String, Class<?>> serverRegister = new ConcurrentHashMap<>();

    /**
     * get a Obj in Registry center <br>
     *
     * @param s key
     * @return t element
     */
    public static Class<?> get(String s) {
        return serverRegister.get(s);
    }

    /**
     * add the element to Registry Table <br>
     * if the key already exists, failure, and return before the value of the key. <br>
     * if success return the element
     *
     * @param s   key
     * @param cls element
     * @return final mapped value
     */
    public static Class<?> set(String s, Class<?> cls) {
        return serverRegister.putIfAbsent(s, cls);
    }

    /**
     * add the element to Registry Table <br>
     * if the key already exists, failure, replace it
     *
     * @param s   key
     * @param cls element
     */
    public static Class<?> put(String s, Class<?> cls) {
        return serverRegister.put(s, cls);
    }

    /**
     * clear
     */
    public static void clear() {
        serverRegister.clear();
    }
}
