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

package cn.hippo4j.threadpool.dynamic.api;

import java.util.Map;

/**
 * Thread-pool dynamic refresh.
 */
public interface ThreadPoolDynamicRefresh {

    /**
     * Register configuration center event listener.
     */
    default void registerListener() {
    }

    /**
     * Build thread-pool bootstrap properties.
     *
     * @param configInfo changed configuration properties
     * @return bootstrap properties
     */
    default BootstrapPropertiesInterface buildBootstrapProperties(Map<Object, Object> configInfo) {
        return null;
    }

    /**
     * Dynamic refresh of configuration center data changes.
     *
     * @param content changed data
     */
    default void dynamicRefresh(String content) {
    }

    /**
     * Dynamic refresh of configuration center data changes.
     *
     * @param configFileType config file type
     * @param content        changed data
     */
    default void dynamicRefresh(String configFileType, String content) {
    }

    /**
     * Dynamic refresh.
     *
     * @param content           changed data
     * @param newValueChangeMap new value change map
     */
    default void dynamicRefresh(String content, Map<String, Object> newValueChangeMap) {
    }

    /**
     * Dynamic refresh.
     *
     * @param configFileType    config file type
     * @param content           changed data
     * @param newValueChangeMap new value change map
     */
    default void dynamicRefresh(String configFileType, String content, Map<String, Object> newValueChangeMap) {
    }
}
