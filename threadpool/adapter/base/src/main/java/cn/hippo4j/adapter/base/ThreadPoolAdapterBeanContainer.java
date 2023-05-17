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

package cn.hippo4j.adapter.base;

import cn.hippo4j.core.config.ApplicationContextHolder;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-pool adapter bean container.
 */
public class ThreadPoolAdapterBeanContainer implements InitializingBean {

    /**
     * Store three-party thread pool framework bean instances
     */
    public static final Map<String, ThreadPoolAdapter> THREAD_POOL_ADAPTER_BEAN_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ThreadPoolAdapter> threadPoolAdapterMap = ApplicationContextHolder.getBeansOfType(ThreadPoolAdapter.class);
        threadPoolAdapterMap.forEach((key, val) -> THREAD_POOL_ADAPTER_BEAN_CONTAINER.put(val.mark(), val));
    }
}
