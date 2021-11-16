package cn.hippo4j.config.model.biz.tenant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Tenant query req dto.
 *
 * @author chen.ma
 * @date 2021/6/29 22:28
 */
@Data
public class TenantQueryReqDTO extends Page {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * tenantName
     */
    private String tenantName;

    /**
     * owner
     */
    private String owner;

}
