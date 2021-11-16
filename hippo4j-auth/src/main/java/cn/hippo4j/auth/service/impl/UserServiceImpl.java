package cn.hippo4j.auth.service.impl;

import cn.hippo4j.auth.mapper.UserMapper;
import cn.hippo4j.auth.model.UserInfo;
import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import cn.hippo4j.auth.service.RoleService;
import cn.hippo4j.auth.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public IPage<UserRespDTO> listUser(UserQueryPageReqDTO reqDTO) {
        IPage<UserInfo> selectPage = userMapper.selectPage(reqDTO, null);

        return selectPage.convert(each -> BeanUtil.toBean(each, UserRespDTO.class));
    }

    @Override
    public void addUser(UserReqDTO reqDTO) {
        LambdaQueryWrapper<UserInfo> queryWrapper = Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUserName, reqDTO.getUserName());
        UserInfo existUserInfo = userMapper.selectOne(queryWrapper);
        if (existUserInfo != null) {
            throw new RuntimeException("用户名重复");
        }

        reqDTO.setPassword(bCryptPasswordEncoder.encode(reqDTO.getPassword()));
        UserInfo insertUser = BeanUtil.toBean(reqDTO, UserInfo.class);
        userMapper.insert(insertUser);
    }

    @Override
    public void updateUser(UserReqDTO reqDTO) {
        if (StrUtil.isNotBlank(reqDTO.getPassword())) {
            reqDTO.setPassword(bCryptPasswordEncoder.encode(reqDTO.getPassword()));
        }
        UserInfo updateUser = BeanUtil.toBean(reqDTO, UserInfo.class);

        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, reqDTO.getUserName());
        userMapper.update(updateUser, updateWrapper);
    }

    @Override
    public void deleteUser(String userName) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserName, userName);
        userMapper.delete(updateWrapper);
        // roleService.deleteRole("", userName);
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
