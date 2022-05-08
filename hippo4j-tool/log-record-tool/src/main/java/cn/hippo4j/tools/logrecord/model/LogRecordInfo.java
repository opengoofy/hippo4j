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

package cn.hippo4j.tools.logrecord.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 日志记录实体.
 *
 * @author chen.ma
 * @date 2021/10/24 17:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogRecordInfo {

    private Long id;

    private String tenant;

    @NotBlank(message = "bizKey required")
    @Length(max = 200, message = "appKey max length is 200")
    private String bizKey;

    @NotBlank(message = "bizNo required")
    @Length(max = 200, message = "bizNo max length is 200")
    private String bizNo;

    @NotBlank(message = "operator required")
    @Length(max = 63, message = "operator max length 63")
    private String operator;

    @NotBlank(message = "opAction required")
    @Length(max = 511, message = "operator max length 511")
    private String action;

    private String category;

    private Date createTime;

    private String detail;

}
