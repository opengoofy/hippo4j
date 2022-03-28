package cn.hippo4j.config.model.biz.tenant;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * Tenant save req dto.
 *
 * @author chen.ma
 * @date 2021/6/29 20:40
 */
@Data
public class TenantSaveReqDTO {

    /**
     * tenantId
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tenantId;

    /**
     * tenantName
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tenantName;

    /**
     * tenantDesc
     */
    private String tenantDesc;

    /**
     * owner
     */
    private String owner;

}
