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

package cn.hippo4j.auth.toolkit;

import cn.hippo4j.common.toolkit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class JwtTokenUtilTest {

    private Long userId = 1L;

    private String username = "baymax";

    private String role = "";

    private boolean isRememberMe = true;

    private String token;

    @Before
    public void setUp() {
        token = JwtTokenUtil.createToken(userId, username, role, isRememberMe);
    }

    @Test
    public void createToken() {
        String name = JwtTokenUtil.getUsername(token);
        Assert.isTrue(username.equals(name));
        Integer userId = JwtTokenUtil.getUserId(token);
        String userRole = JwtTokenUtil.getUserRole(token);
        Assert.isTrue(username.equals(name));
        Assert.isTrue(this.userId.intValue() == userId);
        Assert.isTrue(role.equals(userRole));
    }

    @Test
    public void getUsername() {
        String name = JwtTokenUtil.getUsername(token);
        Assert.isTrue(username.equals(name));
    }

    @Test
    public void getUserId() {
        Integer id = JwtTokenUtil.getUserId(token);
        Assert.isTrue(userId.intValue() == id);
    }

    @Test
    public void getUserRole() {
        String userRole = JwtTokenUtil.getUserRole(token);
        Assert.isTrue(role.equals(userRole));
    }

    @Test
    public void isExpiration() {
        boolean isExpiration = JwtTokenUtil.isExpiration(token);
        Assert.isTrue(!isExpiration);
    }
}
