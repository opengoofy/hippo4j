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

import java.util.Date;

/**
 * config modify query resp
 */
@Data
public class ConfigModificationQueryRespDTO {

    /**
     * his_config_verify id
     */
    private String id;

    /**
     * config modify type
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
     * thread pool identify
     */
    private String identify;

    /**
     * modify user
     */
    private String modifyUser;

    /**
     * verify status
     */
    private Integer verifyStatus;

    /**
     * gmt create
     */
    private Date gmtCreate;

    /**
     * gmt verify
     */
    private Date gmtVerify;

    /**
     * verify user
     */
    private String verifyUser;
}
