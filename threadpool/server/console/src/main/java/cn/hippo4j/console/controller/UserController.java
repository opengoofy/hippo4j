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

import cn.hippo4j.auth.model.UserInfo;
import cn.hippo4j.auth.model.biz.permission.PermissionRespDTO;
import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import cn.hippo4j.auth.security.AuthManager;
import cn.hippo4j.auth.service.UserService;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.TokenInfo;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.server.common.base.Results;
import cn.hippo4j.config.service.biz.TenantService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hippo4j.auth.constant.Constants.TOKEN_VALIDITY_IN_SECONDS;
import static cn.hippo4j.common.constant.Constants.ADMIN_USER;

/**
 * User controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/auth/users")
public class UserController {

    private final UserService userService;

    private final AuthManager authManager;

    private final TenantService tenantService;

    @PostMapping("/apply/token")
    public Result<TokenInfo> applyToken(@RequestBody UserInfo userInfo) {
        String accessToken = authManager.resolveTokenFromUser(userInfo.getUserName(), userInfo.getPassword());
        TokenInfo tokenInfo = new TokenInfo(accessToken, TOKEN_VALIDITY_IN_SECONDS);
        return Results.success(tokenInfo);
    }

    @PostMapping("/page")
    public Result<IPage<UserRespDTO>> listUser(@RequestBody UserQueryPageReqDTO reqDTO) {
        IPage<UserRespDTO> resultUserPage = userService.listUser(reqDTO);
        return Results.success(resultUserPage);
    }

    @GetMapping("/info/{username}")
    public Result<UserRespDTO> userInfo(@PathVariable("username") String username) {
        UserRespDTO userRespDTO = userService.getUser(new UserReqDTO().setUserName(username));
        if (Objects.equals(username, ADMIN_USER)) {
            userRespDTO.setResources(tenantService.listAllTenant().stream().map(each -> new PermissionRespDTO(username, each, "rw")).collect(Collectors.toList()));
            userRespDTO.setTempResources(tenantService.listAllTenant());
        }
        return Results.success(userRespDTO);
    }

    @PostMapping("/add")
    public Result<Void> addUser(@Validated @RequestBody UserReqDTO reqDTO) {
        userService.addUser(reqDTO);
        return Results.success();
    }

    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody UserReqDTO reqDTO) {
        userService.updateUser(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/remove/{userName}")
    public Result<Void> deleteUser(@PathVariable("userName") String userName) {
        userService.deleteUser(userName);
        return Results.success();
    }

    @GetMapping("/search/{userName}")
    public Result<List<String>> searchUsersLikeUserName(@PathVariable("userName") String userName) {
        List<String> resultUserNames = userService.getUserLikeUsername(userName);
        return Results.success(resultUserNames);
    }
}
