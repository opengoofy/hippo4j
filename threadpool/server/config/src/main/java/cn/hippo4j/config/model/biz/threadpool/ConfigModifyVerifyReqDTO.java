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

package cn.hippo4j.config.model.biz.threadpool;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * Config modify verify dto
 */
@Data
public class ConfigModifyVerifyReqDTO {

    /**
     * His config verify id
     */
    private String id;

    /**
     * Config verify type
     */
    private Integer type;

    /**
     * Tenant id
     */
    private String tenantId;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Instance identify
     */
    private String identify;

    /**
     * Adapter thread pool mark
     */
    private String mark;

    /**
     * Adapter thread pool key
     */
    private String threadPoolKey;

    /**
     * Thread pool id
     */
    @JsonAlias("threadPoolId")
    private String tpId;

    /**
     * Weather modify all instances
     */
    private Boolean modifyAll;

    /**
     * Weather accept config modification
     */
    private Boolean accept;

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Queue type
     */
    private Integer queueType;

    /**
     * Capacity
     */
    private Integer capacity;

    /**
     * Keep alive time
     */
    private Integer keepAliveTime;

    /**
     * Execute time out
     */
    private Long executeTimeOut;

    /**
     * Rejected type
     */
    private Integer rejectedType;

    /**
     * Is alarm
     */
    private Integer isAlarm;

    /**
     * Capacity alarm
     */
    private Integer capacityAlarm;

    /**
     * Liveness alarm
     */
    @JsonAlias("activeAlarm")
    private Integer livenessAlarm;

    /**
     * Allow core thread timeout
     */
    private Integer allowCoreThreadTimeOut;

}
