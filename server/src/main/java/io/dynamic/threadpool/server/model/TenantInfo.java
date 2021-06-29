package io.dynamic.threadpool.server.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 业务线实体信息
 *
 * @author chen.ma
 * @date 2021/6/29 22:04
 */
@Data
@TableName("tenant_info")
public class TenantInfo {

    /**
     * ID
     */
    private Integer id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户简介
     */
    private String tenantDesc;

    /**
     * 负责人
     */
    private String owner;

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
    @TableLogic
    private Integer delFlag;

}
