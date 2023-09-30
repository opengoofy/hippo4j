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

import org.junit.Assert;
import org.junit.Test;

public class UserServiceImplTest {

    @Test
    public void testCheckPasswordLength() {
        // 密码为null、空串、过短、过长都会抛出异常
        UserServiceImpl userService = new UserServiceImpl(null, null, null);
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(null));
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(""));
        String shortPassword = "12345";
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(shortPassword));
        String LongPassword = "fjhdjfghdsgahfgajdhsgafghdsbvhbervjdsvhdsbhfbhsdbhfbhsdbavbsbdhjfbhjsdbhfbsdbf";
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(LongPassword));
    }

}