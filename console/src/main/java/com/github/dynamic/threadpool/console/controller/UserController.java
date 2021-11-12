package com.github.dynamic.threadpool.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.auth.model.biz.user.UserQueryPageReqDTO;
import com.github.dynamic.threadpool.auth.model.biz.user.UserReqDTO;
import com.github.dynamic.threadpool.auth.model.biz.user.UserRespDTO;
import com.github.dynamic.threadpool.auth.service.UserService;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User controller.
 *
 * @author chen.ma
 * @date 2021/10/30 21:15
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/auth/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/page")
    public Result<IPage<UserRespDTO>> listUser(@RequestBody UserQueryPageReqDTO reqDTO) {
        IPage<UserRespDTO> resultUserPage = userService.listUser(reqDTO);
        return Results.success(resultUserPage);
    }

    @PostMapping("/add")
    public Result<Void> addUser(@RequestBody UserReqDTO reqDTO) {
        userService.addUser(reqDTO);
        return Results.success();
    }

    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody UserReqDTO reqDTO) {
        userService.updateUser(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/remove/{userName}")
    public Result<Void> deleteUser(@PathVariable("userName") String userName) {
        userService.deleteUser(userName);
        return Results.success();
    }

    @GetMapping("/search/{userName}")
    public Result<List<String>> searchUsersLikeUserName(@PathVariable("userName") String userName) {
        List<String> resultUserNames = userService.getUserLikeUsername(userName);
        return Results.success(resultUserNames);
    }

}
