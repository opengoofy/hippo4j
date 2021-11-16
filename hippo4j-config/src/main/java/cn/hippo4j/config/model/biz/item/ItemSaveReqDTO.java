package cn.hippo4j.config.model.biz.item;

import lombok.Data;

/**
 * Item save req dto.
 *
 * @author chen.ma
 * @date 2021/6/29 22:05
 */
@Data
public class ItemSaveReqDTO {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String itemId;

    /**
     * itemName
     */
    private String itemName;

    /**
     * itemDesc
     */
    private String itemDesc;

    /**
     * owner
     */
    private String owner;

}
