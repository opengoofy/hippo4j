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
 * Config modify query resp
 */
@Data
public class ConfigModificationQueryRespDTO {

    /**
     * His_config_verify id
     */
    private String id;

    /**
     * Config modify type
     */
    private Integer type;

    /**
     * Thread pool mark
     */
    private String mark;

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
     * Thread pool identify
     */
    private String identify;

    /**
     * Weather modify all instances
     */
    private Boolean modifyAll;

    /**
     * Modify user
     */
    private String modifyUser;

    /**
     * Verify status
     */
    private Integer verifyStatus;

    /**
     * GmtCreate
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * GmtVerify
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtVerify;

    /**
     * Verify user
     */
    private String verifyUser;
}
