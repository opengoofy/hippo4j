package com.github.dynamic.threadpool.config.model.biz.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 项目出参
 *
 * @author chen.ma
 * @date 2021/6/29 21:15
 */
@Data
public class ItemRespDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 租户 ID
     */
    private String tenantId;

    /**
     * 项目 ID
     */
    private String itemId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目介绍
     */
    private String itemDesc;

    /**
     * 项目负责人
     */
    private String owner;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}
