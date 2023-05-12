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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Log record info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("log_record_info")
public class LogRecordInfo {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Tenant
     */
    private String tenant;

    /**
     * Biz key
     */
    private String bizKey;

    /**
     * Biz no
     */
    private String bizNo;

    /**
     * Operator
     */
    private String operator;

    /**
     * Action
     */
    private String action;

    /**
     * Category
     */
    private String category;

    /**
     * Detail
     */
    private String detail;

    /**
     * Create time
     */
    private Date createTime;
}
