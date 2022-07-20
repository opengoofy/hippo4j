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
 * Thread pool resp dto.
 *
 * @author chen.ma
 * @date 2021/6/30 21:23
 */
@Data
public class ThreadPoolRespDTO {

    /**
     * id
     */
    private String id;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String itemId;

    /**
     * tpId
     */
    private String tpId;

    /**
     * coreSize
     */
    private Integer coreSize;

    /**
     * maxSize
     */
    private Integer maxSize;

    /**
     * queueType
     */
    private Integer queueType;

    /**
     * queueName
     */
    private String queueName;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * keepAliveTime
     */
    private Integer keepAliveTime;

    /**
     * isAlarm
     */
    private Integer isAlarm;

    /**
     * capacityAlarm
     */
    private Integer capacityAlarm;

    /**
     * livenessAlarm
     */
    private Integer livenessAlarm;

    /**
     * rejectedType
     */
    private Integer rejectedType;

    /**
     * allowCoreThreadTimeOut
     */
    private Integer allowCoreThreadTimeOut;

    /**
     * gmtCreate
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * gmtModified
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}
