package cn.hippo4j.auth.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * Permission info.
 *
 * @author chen.ma
 * @date 2021/10/30 22:33
 */
@Data
@TableName("permission")
public class PermissionInfo {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * role
     */
    private String role;

    /**
     * resource
     */
    private String resource;

    /**
     * action
     */
    private String action;

    /**
     * gmtCreate
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * gmtModified
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * delFlag
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

}
