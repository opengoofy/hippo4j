package cn.hippo4j.config.model.biz.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Item resp dto.
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

    /**
     * gmtCreate
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * gmtModified
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}
