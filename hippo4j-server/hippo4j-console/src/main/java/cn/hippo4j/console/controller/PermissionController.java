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

package cn.hippo4j.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.auth.model.biz.permission.PermissionRespDTO;
import cn.hippo4j.auth.service.PermissionService;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Permission controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/v1/auth/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{pageNo}/{pageSize}")
    public Result<IPage<PermissionRespDTO>> listPermission(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        IPage<PermissionRespDTO> resultPermissionPage = permissionService.listPermission(pageNo, pageSize);
        return Results.success(resultPermissionPage);
    }

    @PostMapping("/{role}/{resource}/{action}")
    public Result<Void> addPermission(@PathVariable("role") String role, @PathVariable("resource") String resource, @PathVariable("action") String action) {
        permissionService.addPermission(role, resource, action);
        return Results.success();
    }

    @DeleteMapping("/{role}/{resource}/{action}")
    public Result<Void> deleteUser(@PathVariable("role") String role, @PathVariable("resource") String resource, @PathVariable("action") String action) {
        permissionService.deletePermission(role, resource, action);
        return Results.success();
    }
}
