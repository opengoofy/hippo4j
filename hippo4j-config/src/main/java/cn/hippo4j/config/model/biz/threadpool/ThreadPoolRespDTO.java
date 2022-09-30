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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Thread pool resp DTO.
 */
@Data
public class ThreadPoolRespDTO {

    /**
     * ID
     */
    private String id;

    /**
     * Tenant id
     */
    private String tenantId;

    /**
     * Iem id
     */
    private String itemId;

    /**
     * Thread-pool id
     */
    private String tpId;

    /**
     * Core size
     */
    private Integer coreSize;

    /**
     * Max size
     */
    private Integer maxSize;

    /**
     * Queue type
     */
    private Integer queueType;

    /**
     * Queue name
     */
    private String queueName;

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
     * AllowCore thread timeout
     */
    private Integer allowCoreThreadTimeOut;

    /**
     * Gmt create
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * Gmt modified
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}
