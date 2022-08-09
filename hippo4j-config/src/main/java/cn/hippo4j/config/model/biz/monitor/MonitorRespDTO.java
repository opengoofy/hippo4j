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

package cn.hippo4j.config.model.biz.monitor;

import lombok.Data;

/**
 * Monitor resp DTO.
 */
@Data
public class MonitorRespDTO {

    /**
     * Tenant id
     */
    private String tenantId;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Instance id
     */
    private String instanceId;

    /**
     * Completed task count
     */
    private String completedTaskCount;

    /**
     * Thread-pool id
     */
    private String tpId;

    /**
     * Current load
     */
    private String currentLoad;

    /**
     * Peak load
     */
    private String peakLoad;

    /**
     * Pool size
     */
    private String poolSize;

    /**
     * Active size
     */
    private String activeSize;

    /**
     * Queue capacity
     */
    private String queueCapacity;

    /**
     * Queue size
     */
    private String queueSize;

    /**
     * Queue remaining capacity
     */
    private String queueRemainingCapacity;

    /**
     * Reject count
     */
    private String rejectCount;
}
