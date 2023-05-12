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

package cn.hippo4j.common.executor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Thread-pool manager.
 */
public final class ThreadPoolManager {

    private Map<String, Map<String, Set<ExecutorService>>> resourcesManager;

    private Map<String, Object> lockers = new ConcurrentHashMap();

    private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return INSTANCE;
    }

    private ThreadPoolManager() {
    }

    static {
        INSTANCE.init();
    }

    private void init() {
        resourcesManager = new ConcurrentHashMap();
    }

    public void register(String tenantId, String group, ExecutorService executor) {
        if (!resourcesManager.containsKey(tenantId)) {
            synchronized (this) {
                lockers.put(tenantId, new Object());
            }
        }
        final Object monitor = lockers.get(tenantId);
        synchronized (monitor) {
            Map<String, Set<ExecutorService>> map = resourcesManager.get(tenantId);
            if (map == null) {
                map = new HashMap();
                map.put(group, new HashSet());
                map.get(group).add(executor);
                resourcesManager.put(tenantId, map);
                return;
            }
            if (!map.containsKey(group)) {
                map.put(group, new HashSet());
            }
            map.get(group).add(executor);
        }
    }
}
