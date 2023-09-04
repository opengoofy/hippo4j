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

package cn.hippo4j.auth.security;

import cn.hippo4j.auth.model.biz.user.LoginUser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.expression.AccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Auth manager.
 */
@Component
@AllArgsConstructor
public class AuthManager {

    private final JwtTokenManager jwtTokenManager;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    /**
     * Resolve token from user.
     *
     * @param userName
     * @param rawPassword
     * @return
     * @throws AccessException
     */
    @SneakyThrows
    public String resolveTokenFromUser(String userName, String rawPassword) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            LoginUser loginUser = new LoginUser();
            loginUser.setPassword(rawPassword);
            request.setAttribute("loginUser", loginUser);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        } catch (AuthenticationException e) {
            throw new AccessException("Unknown user.");
        }
        return jwtTokenManager.createToken(userName);
    }
}
