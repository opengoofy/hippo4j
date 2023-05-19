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

package cn.hippo4j.core.executor.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Plug in runtime information.
 */
@RequiredArgsConstructor
@Getter
public class PluginRuntime {

    /**
     * Plugin id
     */
    private final String pluginId;

    /**
     * Runtime info
     */
    private final List<Info> infoList = new ArrayList<>();

    /**
     * Add a runtime info item.
     *
     * @param name  name
     * @param value value
     * @return runtime info item
     */
    public PluginRuntime addInfo(String name, Object value) {
        infoList.add(new Info(name, value));
        return this;
    }

    /**
     * Plugin runtime info.
     */
    @Getter
    @RequiredArgsConstructor
    public static class Info {

        /**
         * Name
         */
        private final String name;

        /**
         * Value
         */
        private final Object value;
    }
}
