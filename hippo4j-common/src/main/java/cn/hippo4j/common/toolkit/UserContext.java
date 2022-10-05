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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * User context (Transition scheme).
 */
public class UserContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserInfo(String username, String userRole) {
        USER_THREAD_LOCAL.set(new User(username, userRole));
    }

    public static String getUserName() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get()).map(User::getUsername).orElse("");
    }

    public static String getUserRole() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get()).map(User::getUserRole).orElse("");
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    /**
     * User info.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {

        private String username;

        private String userRole;
    }
}
