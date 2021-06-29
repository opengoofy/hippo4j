package io.dynamic.threadpool.server.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Item Info.
 *
 * @author chen.ma
 * @date 2021/6/29 21:53
 */
@Data
@TableName("item_info")
public class ItemInfo {

    private Integer id;

    private String tenantId;

    private String itemId;

    private String itemName;

    private String itemDesc;

    private String owner;

    private Date gmtCreate;

    private Date gmtModified;
}
