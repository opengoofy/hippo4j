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

import cn.hippo4j.auth.security.JwtTokenManager;
import cn.hippo4j.auth.toolkit.JwtTokenUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.server.common.base.Results;
import cn.hippo4j.server.common.base.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static cn.hippo4j.common.constant.Constants.ACCESS_TOKEN;
import static cn.hippo4j.server.common.base.exception.ErrorCodeEnum.LOGIN_TIMEOUT;

/**
 * JWT authorization filter.
 */
@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenManager tokenManager;

    public JWTAuthorizationFilter(JwtTokenManager tokenManager, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        boolean checkAccessTokenOrTokenHeader = false;
        // Token when verifying client interaction.
        String accessToken = request.getParameter(ACCESS_TOKEN);
        String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        if (StringUtil.isNotBlank(accessToken)) {
            tokenManager.validateToken(accessToken);
            Authentication authentication = this.tokenManager.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            checkAccessTokenOrTokenHeader = true;
        } else if (checkTokenHeader(tokenHeader)) {
            // If there is no Authorization information in the request header, it will be released directly.
            checkAccessTokenOrTokenHeader = true;
        }
        if (checkAccessTokenOrTokenHeader) {
            chain.doFilter(request, response);
        } else {
            filterInternal(request, response, chain, tokenHeader);
        }
    }

    private void filterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                String tokenHeader) throws IOException, ServletException {
        try {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
        } catch (Exception ex) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            String resultStatus = "-1";
            if (ex instanceof ServiceException) {
                ServiceException serviceException = (ServiceException) ex;
                resultStatus = serviceException.getErrorCode().getCode();
            }
            response.getWriter().write(JSONUtil.toJSONString(Results.failure(resultStatus, ex.getMessage())));
            response.getWriter().flush();
            return;
        }
        try {
            super.doFilterInternal(request, response, chain);
        } finally {
            UserContext.clear();
        }
    }

    private boolean checkTokenHeader(String tokenHeader) {
        return tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX);
    }

    /**
     * Obtain user information from Token and create a new Token.
     *
     * @param tokenHeader tokenHeader
     * @return UsernamePasswordAuthenticationToken
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {
        String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        boolean expiration = JwtTokenUtil.isExpiration(token);
        if (expiration) {
            throw new ServiceException(LOGIN_TIMEOUT);
        }
        String username = JwtTokenUtil.getUsername(token);
        String userRole = JwtTokenUtil.getUserRole(token);
        UserContext.setUserInfo(username, userRole);
        String role = JwtTokenUtil.getUserRole(token);
        if (username != null) {
            return new UsernamePasswordAuthenticationToken(username, null,
                    Collections.singleton(new SimpleGrantedAuthority(role)));
        }
        return null;
    }
}
