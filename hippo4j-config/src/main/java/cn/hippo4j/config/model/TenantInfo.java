package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * Tenant info.
 *
 * @author chen.ma
 * @date 2021/6/29 22:04
 */
@Data
@TableName("tenant")
public class TenantInfo {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * tenantName
     */
    private String tenantName;

    /**
     * tenantDesc
     */
    private String tenantDesc;

    /**
     * owner
     */
    private String owner;

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
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

}
