package cn.hippo4j.config.model.biz.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Tenant resp dto.
 *
 * @author chen.ma
 * @date 2021/6/29 21:16
 */
@Data
public class TenantRespDTO {

    /**
     * ID
     */
    private Integer id;

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
