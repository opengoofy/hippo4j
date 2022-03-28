package cn.hippo4j.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.auth.model.biz.role.RoleRespDTO;

import java.util.List;

/**
 * Role service.
 *
 * @author chen.ma
 * @date 2021/10/30 22:45
 */
public interface RoleService {

    /**
     * 分页查询角色列表.
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<RoleRespDTO> listRole(int pageNo, int pageSize);

    /**
     * 新增角色.
     *
     * @param role
     * @param userName
     */
    void addRole(String role, String userName);

    /**
     * 删除角色.
     *
     * @param role
     * @param userName
     */
    void deleteRole(String role, String userName);

    /**
     * 根据角色模糊搜索.
     *
     * @param role
     * @return
     */
    List<String> getRoleLike(String role);

}
