package cn.hippo4j.console.controller;

import cn.hippo4j.auth.model.UserInfo;
import cn.hippo4j.auth.model.biz.user.UserQueryPageReqDTO;
import cn.hippo4j.auth.model.biz.user.UserReqDTO;
import cn.hippo4j.auth.model.biz.user.UserRespDTO;
import cn.hippo4j.auth.security.AuthManager;
import cn.hippo4j.auth.service.UserService;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.TokenInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.hippo4j.auth.constant.Constants.TOKEN_VALIDITY_IN_SECONDS;

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

    private final AuthManager authManager;

    @PostMapping("/apply/token")
    public Result<TokenInfo> applyToken(@RequestBody UserInfo userInfo) {
        String accessToken = authManager.resolveTokenFromUser(userInfo.getUserName(), userInfo.getPassword());
        TokenInfo tokenInfo = new TokenInfo(accessToken, TOKEN_VALIDITY_IN_SECONDS);
        return Results.success(tokenInfo);
    }

    @PostMapping("/page")
    public Result<IPage<UserRespDTO>> listUser(@RequestBody UserQueryPageReqDTO reqDTO) {
        IPage<UserRespDTO> resultUserPage = userService.listUser(reqDTO);
        return Results.success(resultUserPage);
    }

    @GetMapping("/info/{username}")
    public Result<UserRespDTO> userInfo(@PathVariable("username") String username) {
        UserRespDTO userRespDTO = userService.getUser(new UserReqDTO().setUserName(username));
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
