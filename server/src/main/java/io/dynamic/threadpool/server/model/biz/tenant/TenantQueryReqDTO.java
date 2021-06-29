package io.dynamic.threadpool.server.model.biz.tenant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Tenant Query Req DTO.
 *
 * @author chen.ma
 * @date 2021/6/29 22:28
 */
@Data
public class TenantQueryReqDTO extends Page {

    private String tenantId;

    private String tenantName;

    private String owner;
}
