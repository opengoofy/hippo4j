package cn.hippo4j.config.model.biz.item;

import lombok.Data;

import javax.validation.constraints.Pattern;


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
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tenantId;

    /**
     * itemId
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
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
