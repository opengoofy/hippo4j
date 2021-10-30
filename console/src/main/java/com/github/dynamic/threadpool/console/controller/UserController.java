package com.github.dynamic.threadpool.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.auth.model.biz.user.UserRespDTO;
import com.github.dynamic.threadpool.auth.service.UserService;
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
@RequestMapping({"/v1/auth", "/v1/auth/users"})
public class UserController {

    private final UserService userService;

    @GetMapping("/{pageNo}/{pageSize}")
    public Result<IPage<UserRespDTO>> listUser(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        IPage<UserRespDTO> resultUserPage = userService.listUser(pageNo, pageSize);
        return Results.success(resultUserPage);
    }

    @PostMapping("/{userName}/{password}")
    public Result<Void> addUser(@PathVariable("userName") String userName, @PathVariable("password") String password) {
        userService.addUser(userName, password);
        return Results.success();
    }

    @PutMapping("/{userName}/{password}")
    public Result<Void> updateUser(@PathVariable("userName") String userName, @PathVariable("password") String password) {
        userService.updateUser(userName, password);
        return Results.success();
    }

    @DeleteMapping("/{userName}")
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
