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
     * @param reqDTO
     * @return
     */
    IPage<UserRespDTO> listUser(UserQueryPageReqDTO reqDTO);

    /**
     * New users.
     *
     * @param reqDTO
     */
    void addUser(UserReqDTO reqDTO);

    /**
     * Modify user.
     *
     * @param reqDTO
     */
    void updateUser(UserReqDTO reqDTO);

    /**
     * Delete users.
     *
     * @param userName
     */
    void deleteUser(String userName);

    /**
     * Fuzzy search by username.
     *
     * @param userName
     * @return
     */
    List<String> getUserLikeUsername(String userName);

    /**
     * Get user details.
     *
     * @param reqDTO
     * @return
     */
    UserRespDTO getUser(UserReqDTO reqDTO);
}
