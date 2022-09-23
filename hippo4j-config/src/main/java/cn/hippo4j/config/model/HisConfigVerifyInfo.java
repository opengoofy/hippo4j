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

import cn.hippo4j.common.model.ThreadPoolParameter;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * his config verify info
 */
@Data
@TableName("his_config_verify")
public class HisConfigVerifyInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * change type
     */
    private Integer type;

    /**
     * tenant id
     */
    private String tenantId;

    /**
     * item id
     */
    private String itemId;

    /**
     * thread pool id
     */
    private String tpId;

    /**
     * thread pool mark
     */
    private String mark;

    /**
     * thread pool instance identify
     */
    private String identify;

    /**
     * config content
     */
    private String content;

    /**
     * weather modify all thread pool instances
     */
    private Boolean modifyAll = false;

    /**
     * gmtCreate
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * modifyUserId
     */
    private String modifyUser;

    /**
     * verify status
     */
    private Integer verifyStatus;

    /**
     * gmtVerify
     */
    @JsonIgnore
    @TableField(fill = FieldFill.UPDATE)
    private Date gmtVerify;

    /**
     * verifyUser
     */
    private String verifyUser;
}
