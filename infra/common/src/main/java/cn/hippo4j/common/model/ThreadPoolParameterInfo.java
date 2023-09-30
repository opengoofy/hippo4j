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

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Thread pool parameter info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreadPoolParameterInfo implements ThreadPoolParameter, Serializable {

    private static final long serialVersionUID = -7123935122108553864L;

    /**
     * Tenant id
     */
    private String tenantId;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Thread-pool id
     */
    @JsonAlias("threadPoolId")
    private String tpId;

    /**
     * Content
     */
    private String content;

    /**
     * Core size
     */
    @Deprecated
    private Integer coreSize;

    /**
     * Max size
     */
    @Deprecated
    private Integer maxSize;

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
    private Long keepAliveTime;

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

    public Integer corePoolSizeAdapt() {
        return this.corePoolSize == null ? this.coreSize : this.corePoolSize;
    }

    public Integer maximumPoolSizeAdapt() {
        return this.maximumPoolSize == null ? this.maxSize : this.maximumPoolSize;
    }
}
