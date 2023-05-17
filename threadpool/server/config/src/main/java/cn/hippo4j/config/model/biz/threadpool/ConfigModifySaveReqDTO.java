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

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * Config Modify Save Req DTO
 */
@Data
public class ConfigModifySaveReqDTO {

    /**
     * Thread pool config change type.
     */
    private Integer type;

    /**
     * Thread pool instance id
     */
    private String identify;

    /**
     * Weather modify all instances
     */
    private Boolean modifyAll = false;

    /**
     * Tenant Id
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tenantId;

    /**
     * Thread pool id
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tpId;

    /**
     * Item id
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String itemId;

    /**
     * Thread pool mark
     */
    private String mark;

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
    private Integer livenessAlarm;

    /**
     * Rejected type
     */
    private Integer rejectedType;

    /**
     * Allow core thread timeout
     */
    private Integer allowCoreThreadTimeOut;

    /**
     * ModifyUser
     */
    private String modifyUser;
}
