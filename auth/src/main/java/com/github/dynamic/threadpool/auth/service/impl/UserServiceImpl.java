package com.github.dynamic.threadpool.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dynamic.threadpool.auth.mapper.UserMapper;
import com.github.dynamic.threadpool.auth.model.UserInfo;
import com.github.dynamic.threadpool.auth.model.biz.user.UserQueryPageReqDTO;
import com.github.dynamic.threadpool.auth.model.biz.user.UserRespDTO;
import com.github.dynamic.threadpool.auth.service.RoleService;
import com.github.dynamic.threadpool.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User service impl.
 *
 * @author chen.ma
 * @date 2021/10/30 21:40
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final RoleService roleService;

    @Override
    public IPage<UserRespDTO> listUser(int pageNo, int pageSize) {
        UserQueryPageReqDTO queryPage = new UserQueryPageReqDTO(pageNo, pageSize);
        IPage<UserInfo> selectPage = userMapper.selectPage(queryPage, null);

        return selectPage.convert(each -> BeanUtil.toBean(each, UserRespDTO.class));
    }

    @Override
    public void addUser(String userName, String password) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUserName, userName);
        UserInfo existUserInfo = userMapper.selectOne(queryWrapper);
        if (existUserInfo != null) {
            throw new RuntimeException("用户名重复");
        }

        UserInfo insertUser = new UserInfo();
        insertUser.setUserName(userName);
        // TODO 暂定为 Md5 加密
        insertUser.setPassword(SecureUtil.md5(password));
        userMapper.insert(insertUser);
    }

    @Override
    public void updateUser(String userName, String password) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setPassword(SecureUtil.md5(password));

        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, userName);
        userMapper.update(userInfo, updateWrapper);
    }

    @Override
    public void deleteUser(String userName) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, userName);
        userMapper.delete(updateWrapper);
        roleService.deleteRole("", userName);
    }

    @Override
    public List<String> getUserLikeUsername(String userName) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .like(UserInfo::getUserName, userName)
                .select(UserInfo::getUserName);

        List<UserInfo> userInfos = userMapper.selectList(queryWrapper);
        List<String> userNames = userInfos.stream().map(UserInfo::getUserName).collect(Collectors.toList());

        return userNames;
    }

}
