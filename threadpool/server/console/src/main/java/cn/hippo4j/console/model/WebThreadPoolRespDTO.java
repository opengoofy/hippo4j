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

package cn.hippo4j.console.model;

import lombok.Data;

/**
 * Web thread-pool resp dto.
 */
@Data
public class WebThreadPoolRespDTO {

    /**
     * thread pool tenant id
     */
    private String tenantId;

    /**
     * item id
     */
    private String itemId;

    /**
     * Active
     */
    private String active;

    /**
     * Identify
     */
    private String identify;

    /**
     * Client address
     */
    private String clientAddress;

    /**
     * Core size
     */
    private Integer coreSize;

    /**
     * Maximum size
     */
    private Integer maximumSize;

    /**
     * Queue type
     */
    private String queueType;

    /**
     * Queue capacity
     */
    private Integer queueCapacity;

    /**
     * Rejected name
     */
    private String rejectedName;

    /**
     * Keep alive time
     */
    private Long keepAliveTime;
}
