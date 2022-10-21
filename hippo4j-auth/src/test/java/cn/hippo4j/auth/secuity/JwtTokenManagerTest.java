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

package cn.hippo4j.auth.secuity;

import cn.hippo4j.auth.security.JwtTokenManager;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public final class JwtTokenManagerTest {

    private static final String USERNAME = "test";

    @Test
    public void createTokenTest() {
        JwtTokenManager jwtTokenManager = new JwtTokenManager();
        String token = jwtTokenManager.createToken(USERNAME);
        Assert.isTrue(StringUtil.isNotBlank(token));
    }

    @Test
    public void validateTokenTest() {
        JwtTokenManager jwtTokenManager = new JwtTokenManager();
        String token = jwtTokenManager.createToken(USERNAME);
        jwtTokenManager.validateToken(token);
    }

    @Test
    public void getAuthenticationTest() {
        JwtTokenManager jwtTokenManager = new JwtTokenManager();
        String token = jwtTokenManager.createToken(USERNAME);
        Authentication authentication = jwtTokenManager.getAuthentication(token);
        Assert.isTrue(authentication.isAuthenticated());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Assert.isTrue(CollectionUtil.isEmpty(authorities));
        Object credentials = authentication.getCredentials();
        Assert.isTrue(ObjectUtils.isEmpty(credentials));
        Object details = authentication.getDetails();
        Assert.isTrue(ObjectUtils.isEmpty(details));
        Object principal = authentication.getPrincipal();
        Assert.isTrue(ObjectUtils.isNotEmpty(principal));
        String name = authentication.getName();
        Assert.isTrue(StringUtil.isNotBlank(name));
    }
}
