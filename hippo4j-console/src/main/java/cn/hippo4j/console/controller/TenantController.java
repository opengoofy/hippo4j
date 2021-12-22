package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.biz.tenant.TenantQueryReqDTO;
import cn.hippo4j.config.model.biz.tenant.TenantRespDTO;
import cn.hippo4j.config.model.biz.tenant.TenantSaveReqDTO;
import cn.hippo4j.config.model.biz.tenant.TenantUpdateReqDTO;
import cn.hippo4j.config.service.biz.TenantService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
        IPage<TenantRespDTO> resultPage = tenantService.queryTenantPage(reqDTO);
        return Results.success(resultPage);
    }

    @GetMapping("/query/{tenantId}")
    public Result<TenantRespDTO> queryNameSpace(@PathVariable("tenantId") String tenantId) {
        return Results.success(tenantService.getTenantByTenantId(tenantId));
    }

    @PostMapping("/save")
    public Result<Boolean> saveNameSpace(@Validated @RequestBody TenantSaveReqDTO reqDTO) {
        tenantService.saveTenant(reqDTO);
        return Results.success(Boolean.TRUE);
    }

    @PostMapping("/update")
    public Result<Boolean> updateNameSpace(@RequestBody TenantUpdateReqDTO reqDTO) {
        tenantService.updateTenant(reqDTO);
        return Results.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete/{tenantId}")
    public Result<Boolean> deleteNameSpace(@PathVariable("tenantId") String tenantId) {
        tenantService.deleteTenantById(tenantId);
        return Results.success(Boolean.TRUE);
    }

}
