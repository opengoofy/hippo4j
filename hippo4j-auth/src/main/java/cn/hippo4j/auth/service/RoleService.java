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

package cn.hippo4j.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.auth.model.biz.role.RoleRespDTO;

import java.util.List;

/**
 * Role service.
 */
public interface RoleService {

    /**
     * Paging query role list.
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<RoleRespDTO> listRole(int pageNo, int pageSize);

    /**
     * Add role.
     *
     * @param role
     * @param userName
     */
    void addRole(String role, String userName);

    /**
     * Delete role.
     *
     * @param role
     * @param userName
     */
    void deleteRole(String role, String userName);

    /**
     * Fuzzy search by role.
     *
     * @param role
     * @return
     */
    List<String> getRoleLike(String role);
}
