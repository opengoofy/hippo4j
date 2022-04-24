package cn.hippo4j.auth.model.biz.permission;

import lombok.Data;

/**
 * Permission resp dto.
 *
 * @author chen.ma
 * @date 2021/10/30 22:21
 */
@Data
public class PermissionRespDTO {

    /**
     * role
     */
    private String role;

    /**
     * source
     */
    private String resource;

    /**
     * action
     */
    private String action;

}
