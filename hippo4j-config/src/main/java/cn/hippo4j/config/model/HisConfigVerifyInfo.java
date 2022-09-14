package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * his config verify info
 */
@Data
@TableName("his_config_verify")
public class HisConfigVerifyInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * change type
     */
    private Integer type;

    /**
     * tenant id
     */
    private String tenantId;

    /**
     * item id
     */
    private String itemId;

    /**
     * thread pool id
     */
    private String tpId;

    /**
     * thread pool instance id
     */
    private String instanceId;

    /**
     * config content
     */
    private String content;

    /**
     * weather modify all thread pool instances
     */
    private Integer modifyAll;

    /**
     * gmtCreate
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * modifyUserId
     */
    private Long modifyUserId;

    /**
     * verify status
     */
    private Integer verifyStatus;

    /**
     * gmtVerify
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date gmtVerify;

    /**
     * verifyUserId
     */
    private Long verifyUserId;
}
