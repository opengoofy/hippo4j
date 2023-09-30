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
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * The staging results<br>
 * The unique remote call can be determined by the key of request and
 * response, and the result of the call is stored in the secondary cache,
 * which is convenient for the client to use at any time.
 *
 * @since 2.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultHolder {

    private static final Map<String, Object> MAP = new ConcurrentHashMap<>();
    private static final Map<String, Thread> THREAD_MAP = new HashMap<>();

    /**
     * Writes when the client receives a response
     *
     * @param key Request and response keys
     * @param o   The result
     */
    public static void put(String key, Object o) {
        if (log.isDebugEnabled()) {
            log.debug("Write the result, wake up the thread");
        }
        MAP.put(key, o);
    }

    /**
     * Stores a thread that can be woken up and is in a waiting state
     *
     * @param key Request and response keys
     * @param t   The Thread
     */
    public static void putThread(String key, Thread t) {
        if (log.isDebugEnabled()) {
            log.debug("Write thread, waiting to wake up");
        }
        THREAD_MAP.put(key, t);
    }

    /**
     * Stores a thread that can be woken up and is in a waiting state
     *
     * @param key Request and response keys
     */
    public static synchronized void wake(String key) {
        if (log.isDebugEnabled()) {
            log.debug("The future has been fetched, wake up the thread");
        }
        Thread thread = THREAD_MAP.remove(key);
        LockSupport.unpark(thread);
    }

    /**
     * Called when the client gets the response<br>
     * After the result is obtained, the corresponding key is cleared from the cache<br>
     * So it's only true when you first get the result
     *
     * @param key Request and response keys
     * @return Response body
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        if (log.isDebugEnabled()) {
            log.debug("Get the future");
        }
        return (T) MAP.remove(key);
    }

}
