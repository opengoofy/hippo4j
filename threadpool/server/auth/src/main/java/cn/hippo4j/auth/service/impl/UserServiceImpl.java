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

package cn.hippo4j.auth.service.impl;

import cn.hippo4j.auth.mapper.UserMapper;
import cn.hippo4j.auth.model.UserInfo;
import cn.hippo4j.auth.model.biz.permission.PermissionRespDTO;
import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import cn.hippo4j.auth.service.PermissionService;
import cn.hippo4j.auth.service.UserService;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.server.common.base.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User service impl.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MINI_PASSWORD_LENGTH = 6;

    private static final int MAX_PASSWORD_LENGTH = 72;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final PermissionService permissionService;

    @Override
    public IPage<UserRespDTO> listUser(UserQueryPageReqDTO requestParam) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .eq(StringUtil.isNotBlank(requestParam.getUserName()), UserInfo::getUserName, requestParam.getUserName());
        IPage<UserInfo> selectPage = userMapper.selectPage(requestParam, queryWrapper);
        return selectPage.convert(this::buildUserInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserReqDTO requestParam) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUserName, requestParam.getUserName());
        UserInfo existUserInfo = userMapper.selectOne(queryWrapper);
        if (existUserInfo != null) {
            throw new RuntimeException("用户名重复");
        }
        this.checkPasswordLength(requestParam.getPassword());
        requestParam.setPassword(bCryptPasswordEncoder.encode(requestParam.getPassword()));
        UserInfo insertUser = BeanUtil.convert(requestParam, UserInfo.class);
        userMapper.insert(insertUser);
        permissionService.bindingPermissionByUsername(requestParam.getUserName(), requestParam.getResources());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserReqDTO requestParam) {
        if (StringUtil.isNotBlank(requestParam.getPassword())) {
            this.checkPasswordLength(requestParam.getPassword());
            requestParam.setPassword(bCryptPasswordEncoder.encode(requestParam.getPassword()));
        }
        UserInfo updateUser = BeanUtil.convert(requestParam, UserInfo.class);
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, requestParam.getUserName());
        userMapper.update(updateUser, updateWrapper);
        permissionService.bindingPermissionByUsername(requestParam.getUserName(), requestParam.getResources());
    }

    @Override
    public void deleteUser(String userName) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, userName);
        userMapper.delete(updateWrapper);
        permissionService.deletePermission(userName);
    }

    @Override
    public List<String> getUserLikeUsername(String userName) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .like(UserInfo::getUserName, userName)
                .select(UserInfo::getUserName);
        List<UserInfo> userInfos = userMapper.selectList(queryWrapper);
        return userInfos.stream().map(UserInfo::getUserName).collect(Collectors.toList());
    }

    @Override
    public UserRespDTO getUser(UserReqDTO requestParam) {
        Wrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class).eq(UserInfo::getUserName, requestParam.getUserName());
        UserInfo userInfo = userMapper.selectOne(queryWrapper);
        return Optional.ofNullable(userInfo)
                .map(this::buildUserInfo)
                .orElseThrow(() -> new ServiceException("查询无此用户, 可以尝试清空缓存或退出登录"));
    }

    private UserRespDTO buildUserInfo(UserInfo userInfo) {
        UserRespDTO result = BeanUtil.convert(userInfo, UserRespDTO.class);
        List<PermissionRespDTO> permissionRespList = permissionService.listPermissionByUserName(result.getUserName());
        result.setResources(permissionRespList);
        result.setTempResources(permissionRespList.stream().map(PermissionRespDTO::getResource).collect(Collectors.toList()));
        return result;
    }

    protected void checkPasswordLength(String password) {
        if (StringUtil.isBlank(password)) {
            throw new RuntimeException("密码不可为空");
        }
        if (password.length() < MINI_PASSWORD_LENGTH) {
            throw new RuntimeException("密码最少为6个字符");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new RuntimeException("密码最多为72个字符");
        }
    }

}
