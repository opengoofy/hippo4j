package com.github.dynamic.threadpool.config.model.biz.item;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Item Query Req DTO.
 *
 * @author chen.ma
 * @date 2021/6/29 22:28
 */
@Data
public class ItemQueryReqDTO extends Page {

    private String tenantId;

    private String itemId;

    private String itemName;

    private String owner;
}
