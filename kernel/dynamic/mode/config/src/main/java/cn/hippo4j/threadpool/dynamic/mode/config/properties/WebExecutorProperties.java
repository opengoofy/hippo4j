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

import cn.hippo4j.common.model.executor.ExecutorNotifyProperties;
import cn.hippo4j.common.api.IExecutorProperties;
import lombok.Data;

/**
 * Web thread pool executor properties.
 */
@Data
public class WebExecutorProperties implements IExecutorProperties {

    /**
     * Thread pool id
     */
    private String threadPoolId;

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Keep alive time
     */
    private Long keepAliveTime;

    /**
     * Nodes, application startup is not affect, change properties is effect
     */
    private String nodes;

    /**
     * these propertied is enabled?
     */
    private Boolean enable = true;

    /**
     * Notify config
     */
    private ExecutorNotifyProperties notify;
}
