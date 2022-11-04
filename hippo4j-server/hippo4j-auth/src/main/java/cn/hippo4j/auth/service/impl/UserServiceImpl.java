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
import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import cn.hippo4j.auth.service.UserService;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.web.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User service impl.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public IPage<UserRespDTO> listUser(UserQueryPageReqDTO reqDTO) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .eq(StringUtil.isNotBlank(reqDTO.getUserName()), UserInfo::getUserName, reqDTO.getUserName());
        IPage<UserInfo> selectPage = userMapper.selectPage(reqDTO, queryWrapper);
        return selectPage.convert(each -> BeanUtil.convert(each, UserRespDTO.class));
    }

    @Override
    public void addUser(UserReqDTO reqDTO) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUserName, reqDTO.getUserName());
        UserInfo existUserInfo = userMapper.selectOne(queryWrapper);
        if (existUserInfo != null) {
            throw new RuntimeException("用户名重复");
        }
        reqDTO.setPassword(bCryptPasswordEncoder.encode(reqDTO.getPassword()));
        UserInfo insertUser = BeanUtil.convert(reqDTO, UserInfo.class);
        userMapper.insert(insertUser);
    }

    @Override
    public void updateUser(UserReqDTO reqDTO) {
        if (StringUtil.isNotBlank(reqDTO.getPassword())) {
            reqDTO.setPassword(bCryptPasswordEncoder.encode(reqDTO.getPassword()));
        }
        UserInfo updateUser = BeanUtil.convert(reqDTO, UserInfo.class);
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, reqDTO.getUserName());
        userMapper.update(updateUser, updateWrapper);
    }

    @Override
    public void deleteUser(String userName) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, userName);
        userMapper.delete(updateWrapper);
    }

    @Override
    public List<String> getUserLikeUsername(String userName) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .like(UserInfo::getUserName, userName)
                .select(UserInfo::getUserName);
        List<UserInfo> userInfos = userMapper.selectList(queryWrapper);
        List<String> userNames = userInfos.stream().map(UserInfo::getUserName).collect(Collectors.toList());
        return userNames;
    }

    @Override
    public UserRespDTO getUser(UserReqDTO reqDTO) {
        Wrapper queryWrapper = Wrappers.lambdaQuery(UserInfo.class).eq(UserInfo::getUserName, reqDTO.getUserName());
        UserInfo userInfo = userMapper.selectOne(queryWrapper);
        UserRespDTO respUser = Optional.ofNullable(userInfo)
                .map(each -> BeanUtil.convert(each, UserRespDTO.class))
                .orElseThrow(() -> new ServiceException("查询无此用户, 可以尝试清空缓存或退出登录."));
        return respUser;
    }
}
