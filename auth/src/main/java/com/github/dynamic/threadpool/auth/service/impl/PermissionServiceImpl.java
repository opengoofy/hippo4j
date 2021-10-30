package com.github.dynamic.threadpool.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dynamic.threadpool.auth.mapper.PermissionMapper;
import com.github.dynamic.threadpool.auth.model.PermissionInfo;
import com.github.dynamic.threadpool.auth.model.biz.permission.PermissionQueryPageReqDTO;
import com.github.dynamic.threadpool.auth.model.biz.permission.PermissionRespDTO;
import com.github.dynamic.threadpool.auth.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Permission service impl.
 *
 * @author chen.ma
 * @date 2021/10/30 22:32
 */
@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public IPage<PermissionRespDTO> listPermission(int pageNo, int pageSize) {
        PermissionQueryPageReqDTO queryPage = new PermissionQueryPageReqDTO(pageNo, pageSize);
        IPage<PermissionInfo> selectPage = permissionMapper.selectPage(queryPage, null);

        return selectPage.convert(each -> BeanUtil.toBean(each, PermissionRespDTO.class));
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
                .eq(PermissionInfo::getRole, role)
                .eq(PermissionInfo::getResource, resource)
                .eq(PermissionInfo::getAction, action);

        permissionMapper.delete(updateWrapper);
    }

}
