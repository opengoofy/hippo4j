package io.dynamic.threadpool.server.model.biz.item;

import lombok.Data;

/**
 * Item Update Req DTO.
 *
 * @author chen.ma
 * @date 2021/6/29 22:05
 */
@Data
public class ItemUpdateReqDTO {

    private String namespace;

    private String itemId;

    private String itemName;

    private String itemDesc;

    private String owner;

}
