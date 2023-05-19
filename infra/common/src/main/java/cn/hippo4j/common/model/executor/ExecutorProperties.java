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

package cn.hippo4j.common.model.executor;

import cn.hippo4j.common.api.IExecutorProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Executor properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExecutorProperties implements IExecutorProperties {

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
     * Queue capacity
     */
    private Integer queueCapacity;

    /**
     * Blocking queue
     */
    private String blockingQueue;

    /**
     * Rejected handler
     */
    private String rejectedHandler;

    /**
     * Keep alive time
     */
    private Long keepAliveTime;

    /**
     * Execute timeout
     */
    private Long executeTimeOut;

    /**
     * Allow core thread timeout
     */
    private Boolean allowCoreThreadTimeOut;

    /**
     * Thread name prefix
     */
    private String threadNamePrefix;

    /**
     * Whether to enable thread pool running alarm
     */
    private Boolean alarm;

    /**
     * Active alarm
     */
    private Integer activeAlarm;

    /**
     * Capacity alarm
     */
    private Integer capacityAlarm;

    /**
     * Notify
     */
    private ExecutorNotifyProperties notify;

    /**
     * Nodes, application startup is not affect, change properties is effect
     */
    private String nodes;
}
