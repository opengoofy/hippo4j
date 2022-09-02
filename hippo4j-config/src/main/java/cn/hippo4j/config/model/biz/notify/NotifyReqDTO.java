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

package cn.hippo4j.config.model.biz.notify;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Notify req DTO.
 *
 * @author chen.ma
 * @date 2021/11/18 20:15
 */
@Data
@Accessors(chain = true)
public class NotifyReqDTO {

    /**
     * ID
     */
    private String id;

    /**
     * Ids
     */
    private String ids;

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
     * Config type
     */
    private Boolean configType;

    /**
     * Alarm type
     */
    private Boolean alarmType;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * Interval
     */
    private Integer interval;

    /**
     * Receives
     */
    private String receives;

    /**
     * Enable
     */
    private Integer enable;
}
