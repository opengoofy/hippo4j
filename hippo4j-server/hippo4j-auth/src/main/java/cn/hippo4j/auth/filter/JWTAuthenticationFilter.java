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

package cn.hippo4j.auth.filter;

import cn.hippo4j.auth.model.biz.user.JwtUser;
import cn.hippo4j.auth.model.biz.user.LoginUser;
import cn.hippo4j.auth.toolkit.JwtTokenUtil;
import cn.hippo4j.auth.toolkit.ReturnT;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.web.base.Results;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static cn.hippo4j.auth.constant.Constants.SPLIT_COMMA;
import static cn.hippo4j.common.constant.Constants.BASE_PATH;
import static cn.hippo4j.common.constant.Constants.MAP_INITIAL_CAPACITY;

/**
 * JWT authentication filter.
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final ThreadLocal<Integer> rememberMe = new ThreadLocal();

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl(BASE_PATH + "/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // Get logged in information from the input stream.
        Authentication authenticate = null;
        try {
            LoginUser loginUser = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
            request.setAttribute("loginUser", loginUser);
            rememberMe.set(loginUser.getRememberMe());
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList()));
        } catch (UsernameNotFoundException e) {
            log.warn("User {} not found", e.getMessage());
            throw e;
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Attempt authentication error", e);
        }
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        try {
            JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
            boolean isRemember = rememberMe.get() == 1;
            String role = "";
            Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                role = authority.getAuthority();
            }
            String token = JwtTokenUtil.createToken(jwtUser.getId(), jwtUser.getUsername(), role, isRemember);
            response.setHeader("token", JwtTokenUtil.TOKEN_PREFIX + token);
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> maps = new HashMap<>(MAP_INITIAL_CAPACITY);
            maps.put("data", JwtTokenUtil.TOKEN_PREFIX + token);
            maps.put("roles", role.split(SPLIT_COMMA));
            response.getWriter().write(JSONUtil.toJSONString(Results.success(maps)));
        } finally {
            rememberMe.remove();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONUtil.toJSONString(new ReturnT(ReturnT.JWT_FAIL_CODE, getMessage(failed))));
    }

    /**
     * Return different echo information to the front end according to different exception types
     */
    private String getMessage(AuthenticationException failed) {
        String message = "Server Error";
        if (failed instanceof UsernameNotFoundException) {
            message = "用户不存在";
        } else if (failed instanceof BadCredentialsException) {
            message = "密码错误";
        }
        return message;
    }
}
