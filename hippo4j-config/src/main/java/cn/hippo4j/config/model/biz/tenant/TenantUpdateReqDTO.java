package cn.hippo4j.config.model.biz.tenant;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * Tenant save req dto.
 *
 * @author chen.ma
 * @date 2021/6/29 20:40
 */
@Data
public class TenantUpdateReqDTO {

    /**
     * id
     */
    private Long id;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * tenantName
     */
    private String tenantName;

    /**
     * tenantDesc
     */
    private String tenantDesc;

    /**
     * owner
     */
    private String owner;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
