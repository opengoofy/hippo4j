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

package cn.hippo4j.auth.model.biz.user;

import cn.hippo4j.auth.model.biz.permission.PermissionReqDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * User req dto.
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserReqDTO extends Page {

    /**
     * User name
     */
    @Length(max = 64, message = "用户名最长为64个字符")
    private String userName;

    /**
     * Password
     */
    @Length(min = 6, message = "密码最少为6个字符")
    private String password;

    /**
     * Role
     */
    private String role;

    /**
     * Resources
     */
    private List<PermissionReqDTO> resources;
}
