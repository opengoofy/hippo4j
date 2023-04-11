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

package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * Config info base.
 */
@Data
public class ConfigInfoBase implements Serializable {

    private static final long serialVersionUID = -1892597426099265730L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * TpId
     */
    private String tpId;

    /**
     * ItemId
     */
    private String itemId;

    /**
     * coreSize
     */
    @JsonAlias("corePoolSize")
    private Integer coreSize;

    /**
     * maxSize
     */
    @JsonAlias("maximumPoolSize")
    private Integer maxSize;

    /**
     * queueType
     */
    private Integer queueType;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * keepAliveTime
     */
    private Long keepAliveTime;

    /**
     * executeTimeOut
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long executeTimeOut;

    /**
     * rejectedType
     */
    private Integer rejectedType;

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
     * allowCoreThreadTimeOut
     */
    private Integer allowCoreThreadTimeOut;

    /**
     * MD5
     */
    @JsonIgnore
    private String md5;

    /**
     * content
     */
    @JsonIgnore
    private String content;
}
