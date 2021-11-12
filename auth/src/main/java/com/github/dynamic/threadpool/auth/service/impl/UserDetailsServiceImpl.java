package com.github.dynamic.threadpool.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dynamic.threadpool.auth.mapper.UserMapper;
import com.github.dynamic.threadpool.auth.model.UserInfo;
import com.github.dynamic.threadpool.auth.model.biz.user.JwtUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;

/**
 * User details service impl.
 *
 * @author chen.ma
 * @date 2021/11/9 22:26
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserInfo userInfo = userMapper.selectOne(Wrappers.lambdaQuery(UserInfo.class).eq(UserInfo::getUserName, userName));

        JwtUser jwtUser = new JwtUser();
        jwtUser.setId(userInfo.getId());
        jwtUser.setUsername(userName);
        jwtUser.setPassword(userInfo.getPassword());

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole() + ""));
        jwtUser.setAuthorities(authorities);

        return jwtUser;
    }

}
