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

import cn.hippo4j.auth.mapper.PermissionMapper;
import cn.hippo4j.auth.model.PermissionInfo;
import cn.hippo4j.auth.model.biz.permission.PermissionReqDTO;
import cn.hippo4j.auth.model.biz.permission.PermissionRespDTO;
import cn.hippo4j.auth.service.PermissionService;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Permission service impl.
 */
@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public List<PermissionRespDTO> listPermissionByUserName(String username) {
        LambdaQueryWrapper<PermissionInfo> queryWrapper = Wrappers.lambdaQuery(PermissionInfo.class)
                .eq(PermissionInfo::getUsername, username);
        return BeanUtil.convert(permissionMapper.selectList(queryWrapper), PermissionRespDTO.class);
    }

    @Override
    public void bindingPermissionByUsername(String username, List<PermissionReqDTO> permissionRequestParamList) {
        if (CollectionUtil.isNotEmpty(permissionRequestParamList)) {
            deletePermission(username);
            permissionRequestParamList.forEach(each -> permissionMapper.insert(PermissionInfo.builder().username(username).resource(each.getResource()).action(each.getAction()).build()));
        }
    }

    @Override
    public void deletePermission(String username) {
        permissionMapper.delete(Wrappers.lambdaUpdate(PermissionInfo.class).eq(PermissionInfo::getUsername, username));
    }
}
