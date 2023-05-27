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

package cn.hippo4j.common.toolkit;

import org.junit.Test;

public class UserContextTest {

    private static final String USERNAME = "test";

    private static final String USER_ROLE = "role1";

    @Test
    public void testSetUserInfo() {
        UserContext.setUserInfo(USERNAME, USER_ROLE);
        ThreadLocal<UserContext.User> userThreadLocal = (ThreadLocal<UserContext.User>) ReflectUtil.getFieldValue(UserContext.class, "USER_THREAD_LOCAL");
        Assert.notNull(userThreadLocal.get());
    }

    @Test
    public void testGetUserName() {
        UserContext.setUserInfo(USERNAME, USER_ROLE);
        String userName = UserContext.getUserName();
        Assert.isTrue(USERNAME.equals(userName));
    }

    @Test
    public void testGetUserRole() {
        UserContext.setUserInfo(USERNAME, USER_ROLE);
        String userRole = UserContext.getUserRole();
        Assert.isTrue(USER_ROLE.equals(userRole));
    }

    @Test
    public void testClear() {
        UserContext.setUserInfo(USERNAME, USER_ROLE);
        ThreadLocal<UserContext.User> userThreadLocal = (ThreadLocal<UserContext.User>) ReflectUtil.getFieldValue(UserContext.class, "USER_THREAD_LOCAL");
        Assert.notNull(userThreadLocal.get());
        UserContext.clear();
        Assert.isNull(userThreadLocal.get());
    }

}
