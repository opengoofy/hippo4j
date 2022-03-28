package cn.hippo4j.config.model.biz.item;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Item query req dto.
 *
 * @author chen.ma
 * @date 2021/6/29 22:28
 */
@Data
public class ItemQueryReqDTO extends Page {

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
     * owner
     */
    private String owner;

}
