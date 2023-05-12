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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * His config verify info
 */
@Data
@TableName("his_config_verify")
public class HisConfigVerifyInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Change type
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
     * Thread pool id
     */
    private String tpId;

    /**
     * Thread pool mark
     */
    private String mark;

    /**
     * Thread pool instance identify
     */
    private String identify;

    /**
     * Config content
     */
    private String content;

    /**
     * Weather modify all thread pool instances
     */
    private Boolean modifyAll = false;

    /**
     * GmtCreate
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * ModifyUserId
     */
    private String modifyUser;

    /**
     * Verify status
     */
    private Integer verifyStatus;

    /**
     * GmtVerify
     */
    private Date gmtVerify;

    /**
     * VerifyUser
     */
    private String verifyUser;
}
