package com.github.dynamic.threadpool.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantQueryReqDTO;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantRespDTO;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantSaveReqDTO;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantUpdateReqDTO;
import com.github.dynamic.threadpool.config.service.biz.TenantService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Tenant controller.
 *
 * @author chen.ma
 * @date 2021/6/25 18:31
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/tenant")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/query/page")
    public Result<IPage<TenantRespDTO>> queryNameSpacePage(@RequestBody TenantQueryReqDTO reqDTO) {
        return Results.success(tenantService.queryTenantPage(reqDTO));
    }

    @GetMapping("/query/{tenantId}")
    public Result<TenantRespDTO> queryNameSpace(@PathVariable("tenantId") String tenantId) {
        return Results.success(tenantService.getTenantByTenantId(tenantId));
    }

    @PostMapping("/save")
    public Result saveNameSpace(@RequestBody TenantSaveReqDTO reqDTO) {
        tenantService.saveTenant(reqDTO);
        return Results.success();
    }

    @PostMapping("/update")
    public Result updateNameSpace(@RequestBody TenantUpdateReqDTO reqDTO) {
        tenantService.updateTenant(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/delete/{tenantId}")
    public Result deleteNameSpace(@PathVariable("tenantId") String tenantId) {
        tenantService.deleteTenantById(tenantId);
        return Results.success();
    }

}
