package cn.hippo4j.console.controller;

import cn.hippo4j.common.toolkit.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import cn.hippo4j.auth.service.UserService;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
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

    @GetMapping("/info")
    public Result<UserRespDTO> userInfo() {
        String userName = UserContext.getUserName();
        IPage<UserRespDTO> listUser = userService.listUser(new UserQueryPageReqDTO().setUserName(userName));
        UserRespDTO userRespDTO = listUser.getRecords().get(0);
        return Results.success(userRespDTO);
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
