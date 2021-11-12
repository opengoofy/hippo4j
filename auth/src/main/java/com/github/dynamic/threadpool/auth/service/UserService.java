package com.github.dynamic.threadpool.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.auth.model.biz.user.UserQueryPageReqDTO;
import com.github.dynamic.threadpool.auth.model.biz.user.UserReqDTO;
import com.github.dynamic.threadpool.auth.model.biz.user.UserRespDTO;

import java.util.List;

/**
 * User service.
 *
 * @author chen.ma
 * @date 2021/10/30 21:34
 */
public interface UserService {

    /**
     * 分页查询用户列表.
     *
     * @param reqDTO
     * @return
     */
    IPage<UserRespDTO> listUser(UserQueryPageReqDTO reqDTO);

    /**
     * 新增用户.
     *
     * @param reqDTO
     */
    void addUser(UserReqDTO reqDTO);

    /**
     * 修改用户.
     *
     * @param reqDTO
     */
    void updateUser(UserReqDTO reqDTO);

    /**
     * 删除用户.
     *
     * @param userName
     */
    void deleteUser(String userName);

    /**
     * 根据用户名模糊搜索.
     *
     * @param userName
     * @return
     */
    List<String> getUserLikeUsername(String userName);

}
