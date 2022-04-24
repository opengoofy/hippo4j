package cn.hippo4j.config.service.handler;

import cn.hippo4j.config.model.biz.tenant.TenantRespDTO;
import cn.hippo4j.config.service.biz.TenantService;
import cn.hippo4j.tools.logrecord.service.ParseFunction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 查找原租户名称.
 *
 * @author chen.ma
 * @date 2021/10/24 22:07
 */
@Component
@AllArgsConstructor
public class TenantIdFunctionServiceImpl implements ParseFunction {

    private final TenantService tenantService;

    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return "TENANT";
    }

    @Override
    public String apply(String tenantId) {
        TenantRespDTO tenant = tenantService.getTenantById(tenantId);
        return Optional.ofNullable(tenant).map(TenantRespDTO::getTenantName).orElse("");
    }

}
