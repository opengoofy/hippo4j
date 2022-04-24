package cn.hippo4j.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.auth.model.biz.role.RoleRespDTO;
import cn.hippo4j.auth.service.RoleService;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Role controller.
 *
 * @author chen.ma
 * @date 2021/10/30 22:16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/v1/auth/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{pageNo}/{pageSize}")
    public Result<IPage<RoleRespDTO>> listUser(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        IPage<RoleRespDTO> resultRolePage = roleService.listRole(pageNo, pageSize);
        return Results.success(resultRolePage);
    }

    @PostMapping("/{role}/{userName}")
    public Result<Void> addUser(@PathVariable("role") String role, @PathVariable("userName") String userName) {
        roleService.addRole(role, userName);
        return Results.success();
    }

    @DeleteMapping("/{role}/{userName}")
    public Result<Void> deleteUser(@PathVariable("role") String role, @PathVariable("userName") String userName) {
        roleService.deleteRole(role, userName);
        return Results.success();
    }

    @GetMapping("/search/{role}")
    public Result<List<String>> searchUsersLikeUserName(@PathVariable("role") String role) {
        List<String> resultRole = roleService.getRoleLike(role);
        return Results.success(resultRole);
    }

}
