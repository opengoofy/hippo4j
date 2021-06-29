package io.dynamic.threadpool.server.model.biz.tenant;

import lombok.Data;

import java.util.Date;

/**
 * 业务线出参
 *
 * @author chen.ma
 * @date 2021/6/29 21:16
 */
@Data
public class TenantRespDTO {

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
}
