package cn.hippo4j.auth.service.impl;

import cn.hippo4j.auth.mapper.RoleMapper;
import cn.hippo4j.auth.model.biz.role.RoleQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.role.RoleRespDTO;
import cn.hippo4j.auth.service.PermissionService;
import cn.hippo4j.auth.service.RoleService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.hippo4j.auth.model.RoleInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Role service impl.
 *
 * @author chen.ma
 * @date 2021/10/30 22:53
 */
@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    private final PermissionService permissionService;

    @Override
    public IPage<RoleRespDTO> listRole(int pageNo, int pageSize) {
        RoleQueryPageReqDTO queryPage = new RoleQueryPageReqDTO(pageNo, pageSize);
        IPage<RoleInfo> selectPage = roleMapper.selectPage((IPage)queryPage, null);

        return selectPage.convert(each -> BeanUtil.toBean(each, RoleRespDTO.class));
    }

    @Override
    public void addRole(String role, String userName) {
        LambdaQueryWrapper<RoleInfo> queryWrapper = Wrappers.lambdaQuery(RoleInfo.class)
                .eq(RoleInfo::getRole, role);
        RoleInfo roleInfo = roleMapper.selectOne(queryWrapper);
        if (roleInfo != null) {
            throw new RuntimeException("角色名重复");
        }

        RoleInfo insertRole = new RoleInfo();
        insertRole.setRole(role);
        insertRole.setUserName(userName);
        roleMapper.insert(insertRole);
    }

    @Override
    public void deleteRole(String role, String userName) {
        List<String> roleStrList = CollUtil.toList(role);
        if (StrUtil.isBlank(role)) {
            LambdaQueryWrapper<RoleInfo> queryWrapper = Wrappers.lambdaQuery(RoleInfo.class).eq(RoleInfo::getUserName, userName);
            roleStrList = roleMapper.selectList(queryWrapper).stream().map(RoleInfo::getRole).collect(Collectors.toList());
        }

        LambdaUpdateWrapper<RoleInfo> updateWrapper = Wrappers.lambdaUpdate(RoleInfo.class)
                .eq(StrUtil.isNotBlank(role), RoleInfo::getRole, role)
                .eq(StrUtil.isNotBlank(userName), RoleInfo::getUserName, userName);
        roleMapper.delete(updateWrapper);

        roleStrList.forEach(each -> permissionService.deletePermission(each, "", ""));
    }

    @Override
    public List<String> getRoleLike(String role) {
        LambdaQueryWrapper<RoleInfo> queryWrapper = Wrappers.lambdaQuery(RoleInfo.class)
                .like(RoleInfo::getRole, role)
                .select(RoleInfo::getRole);

        List<RoleInfo> roleInfos = roleMapper.selectList(queryWrapper);
        List<String> roleNames = roleInfos.stream().map(RoleInfo::getRole).collect(Collectors.toList());

        return roleNames;
    }

}
