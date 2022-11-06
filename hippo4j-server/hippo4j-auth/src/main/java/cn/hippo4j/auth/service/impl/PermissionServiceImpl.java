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
import cn.hippo4j.auth.model.biz.permission.PermissionQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.permission.PermissionRespDTO;
import cn.hippo4j.auth.service.PermissionService;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.hippo4j.auth.model.PermissionInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Permission service impl.
 */
@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public IPage<PermissionRespDTO> listPermission(int pageNo, int pageSize) {
        PermissionQueryPageReqDTO queryPage = new PermissionQueryPageReqDTO(pageNo, pageSize);
        IPage<PermissionInfo> selectPage = permissionMapper.selectPage(queryPage, null);
        return selectPage.convert(each -> BeanUtil.convert(each, PermissionRespDTO.class));
    }

    @Override
    public void addPermission(String role, String resource, String action) {
        LambdaQueryWrapper<PermissionInfo> queryWrapper = Wrappers.lambdaQuery(PermissionInfo.class)
                .eq(PermissionInfo::getRole, role)
                .eq(PermissionInfo::getResource, resource)
                .eq(PermissionInfo::getAction, action);
        PermissionInfo existPermissionInfo = permissionMapper.selectOne(queryWrapper);
        if (existPermissionInfo != null) {
            throw new RuntimeException("权限重复");
        }
        PermissionInfo insertPermission = new PermissionInfo();
        insertPermission.setRole(role);
        insertPermission.setResource(resource);
        insertPermission.setAction(action);
        permissionMapper.insert(insertPermission);
    }

    @Override
    public void deletePermission(String role, String resource, String action) {
        LambdaUpdateWrapper<PermissionInfo> updateWrapper = Wrappers.lambdaUpdate(PermissionInfo.class)
                .eq(StringUtil.isNotBlank(role), PermissionInfo::getRole, role)
                .eq(StringUtil.isNotBlank(resource), PermissionInfo::getResource, resource)
                .eq(StringUtil.isNotBlank(action), PermissionInfo::getAction, action);
        permissionMapper.delete(updateWrapper);
    }
}
