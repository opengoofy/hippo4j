package com.github.dynamic.threadpool.config.service.handler;

import com.github.dynamic.threadpool.config.model.biz.tenant.TenantRespDTO;
import com.github.dynamic.threadpool.config.service.biz.TenantService;
import com.github.dynamic.threadpool.logrecord.service.ParseFunction;
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
