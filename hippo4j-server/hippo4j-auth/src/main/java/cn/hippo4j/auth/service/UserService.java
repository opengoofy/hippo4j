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

package cn.hippo4j.auth.service;

import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;

import java.util.List;

/**
 * User service.
 */
public interface UserService {

    /**
     * Paging query user list.
     *
     * @param requestParam request param
     * @return user response page
     */
    IPage<UserRespDTO> listUser(UserQueryPageReqDTO requestParam);

    /**
     * New users.
     *
     * @param requestParam request param
     */
    void addUser(UserReqDTO requestParam);

    /**
     * Modify user.
     *
     * @param requestParam request param
     */
    void updateUser(UserReqDTO requestParam);

    /**
     * Delete users.
     *
     * @param username username
     */
    void deleteUser(String username);

    /**
     * Fuzzy search by username.
     *
     * @param userName userName
     * @return like username
     */
    List<String> getUserLikeUsername(String userName);

    /**
     * Get user details.
     *
     * @param requestParam request param
     * @return user response
     */
    UserRespDTO getUser(UserReqDTO requestParam);
}
