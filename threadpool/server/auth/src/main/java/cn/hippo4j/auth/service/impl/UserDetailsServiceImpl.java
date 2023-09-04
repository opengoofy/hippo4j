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
import cn.hippo4j.auth.model.biz.user.JwtUser;
import cn.hippo4j.auth.model.biz.user.LoginUser;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * User details service impl.
 */
@Slf4j
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${hippo4j.core.auth.enabled:true}")
    private Boolean enableAuthentication;

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        JwtUser anonymous = dealWithAnonymous();
        if (!Objects.isNull(anonymous)) {
            return anonymous;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        String loginPassword = loginUser.getPassword();
        UserInfo userInfo = userMapper.selectOne(Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUserName, userName));
        if (Objects.isNull(userInfo)) {
            throw new UsernameNotFoundException(userName);
        }
        // Validation password
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(loginPassword, userInfo.getPassword())) {
            throw new BadCredentialsException(userName + "密码错误，请重新输入");
        }
        JwtUser jwtUser = new JwtUser();
        jwtUser.setId(userInfo.getId());
        jwtUser.setUsername(userName);
        jwtUser.setPassword(userInfo.getPassword());
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole() + ""));
        jwtUser.setAuthorities(authorities);
        return jwtUser;
    }

    private JwtUser dealWithAnonymous() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        if (Objects.isNull(loginUser)) {
            return null;
        }
        if (Boolean.FALSE.equals(enableAuthentication)) {
            JwtUser jwtUser = new JwtUser();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            jwtUser.setId(1L);
            jwtUser.setUsername("anonymous");
            jwtUser.setPassword(bCryptPasswordEncoder.encode(loginUser.getPassword()));
            Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            jwtUser.setAuthorities(authorities);
            return jwtUser;
        }
        return null;
    }
}
