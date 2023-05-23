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

package cn.hippo4j.threadpool.dynamic.mode.config.properties;

import lombok.Data;

/**
 * Thread pool monitoring properties.
 */
@Data
public class MonitorProperties {

    /**
     * Collect thread pool runtime indicators.
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Type of collection thread pool running data. eg: log,micrometer. Multiple can be used at the same time, default micrometer.
     */
    // TODO
    private String collectTypes = "micrometer";

    /**
     * Monitor the type of thread pool. eg: dynamic,web,adapter. Can be configured arbitrarily, default dynamic.
     */
    // TODO
    private String threadPoolTypes = "dynamic";

    /**
     * Delay starting data acquisition task. unit: ms
     */
    private Long initialDelay = 10000L;

    /**
     * Collect interval. unit: ms
     */
    private Long collectInterval = 5000L;
}
