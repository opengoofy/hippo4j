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

import cn.hippo4j.auth.model.biz.permission.PermissionReqDTO;
import cn.hippo4j.auth.model.biz.permission.PermissionRespDTO;

import java.util.List;

/**
 * Permission service.
 */
public interface PermissionService {

    /**
     * list permission by username
     *
     * @param username username
     * @return permission resp list
     */
    List<PermissionRespDTO> listPermissionByUserName(String username);

    /**
     * Binding permission by username.
     *
     * @param username                   username
     * @param permissionRequestParamList permission request param list
     */
    void bindingPermissionByUsername(String username, List<PermissionReqDTO> permissionRequestParamList);

    /**
     * Remove permission.
     *
     * @param username username
     */
    void deletePermission(String username);
}
