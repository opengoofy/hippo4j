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

package cn.hippo4j.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Plug in runtime information.
 */
@Accessors(chain = true)
@Getter
@Setter
public class PluginRuntimeInfo {

    /**
     * Plugin id
     */
    private String pluginId;

    /**
     * Description of this plugin
     */
    private String description;

    /**
     * Runtime info
     */
    private final List<Info> infoList = new ArrayList<>();

    /**
     * Create a {@link PluginRuntimeInfo}
     */
    public PluginRuntimeInfo() {
        this(null);
    }

    /**
     * Create a {@link PluginRuntimeInfo}
     *
     * @param pluginId plugin id
     */
    public PluginRuntimeInfo(String pluginId) {
        this.pluginId = Objects.isNull(pluginId) ? this.getClass().getSimpleName() : pluginId;
    }

    /**
     * Add a runtime info item.
     *
     * @param name  name
     * @param value value
     * @return runtime info item
     */
    public PluginRuntimeInfo addInfo(String name, Object value) {
        infoList.add(new Info(name, value));
        return this;
    }

    /**
     * Plugin runtime info.
     */
    @AllArgsConstructor
    @Setter
    @Getter
    public static class Info {

        /**
         * Name
         */
        private String name;

        /**
         * Value
         */
        private Object value;

    }
}
