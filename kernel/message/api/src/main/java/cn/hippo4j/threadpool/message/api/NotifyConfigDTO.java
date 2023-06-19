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

package cn.hippo4j.threadpool.message.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Notify config DTO.
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class NotifyConfigDTO {

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
    private String tpId;

    /**
     * Platform
     */
    private String platform;

    /**
     * Type
     */
    private String type;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * Secret
     */
    private String secret;

    /**
     * Interval
     */
    private Integer interval;

    /**
     * Receives
     */
    private String receives;

    /**
     * Type enum
     */
    private NotifyTypeEnum typeEnum;
}
