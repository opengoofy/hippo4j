package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 通知管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:03
 */
@Data
@TableName("notify")
public class NotifyInfo {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 线程池id
     */
    private String tpId;

    /**
     * 通知平台
     */
    private String platform;

    /**
     * 通知类型
     */
    private String type;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 报警间隔
     */
    @TableField("`interval`")
    private Integer interval;

    /**
     * 接收者
     */
    private String receives;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * 是否启用
     */
    private Integer enable;

    /**
     * 是否删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

}
