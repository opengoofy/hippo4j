package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 报警管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:03
 */
@Data
@TableName("alarm")
public class AlarmInfo {

    /**
     * id
     */
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
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 是否删除
     */
    private Integer delFlag;

}
