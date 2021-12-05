package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * Config instance info.
 *
 * @author chen.ma
 * @date 2021/12/5 19:19
 */
@Data
@TableName("inst_config")
public class ConfigInstanceInfo {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String itemId;

    /**
     * tpId
     */
    private String tpId;

    /**
     * instanceId
     */
    private String instanceId;

    /**
     * MD5
     */
    private String md5;

    /**
     * content
     */
    private String content;

    /**
     * gmtCreate
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

}
