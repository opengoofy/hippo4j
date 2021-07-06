package io.dynamic.threadpool.config.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.web.base.Result;
import io.dynamic.threadpool.common.web.base.Results;
import io.dynamic.threadpool.config.model.biz.tenant.TenantQueryReqDTO;
import io.dynamic.threadpool.config.model.biz.tenant.TenantRespDTO;
import io.dynamic.threadpool.config.model.biz.tenant.TenantSaveReqDTO;
import io.dynamic.threadpool.config.model.biz.tenant.TenantUpdateReqDTO;
import io.dynamic.threadpool.config.service.biz.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 业务控制器
 *
 * @author chen.ma
 * @date 2021/6/25 18:31
 */
@RestController
@RequestMapping(Constants.BASE_PATH)
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping("/namespace/query/page")
    public Result<IPage<TenantRespDTO>> queryNameSpacePage(@RequestBody TenantQueryReqDTO reqDTO) {
        return Results.success(tenantService.queryNameSpacePage(reqDTO));
    }

    @GetMapping("/namespace/query/{namespaceId}")
    public Result<TenantRespDTO> queryNameSpace(@PathVariable("namespaceId") String namespaceId) {
        return Results.success(tenantService.getNameSpaceById(namespaceId));
    }

    @PostMapping("/namespace/save")
    public Result saveNameSpace(@RequestBody TenantSaveReqDTO reqDTO) {
        tenantService.saveNameSpace(reqDTO);
        return Results.success();
    }

    @PostMapping("/namespace/update")
    public Result updateNameSpace(@RequestBody TenantUpdateReqDTO reqDTO) {
        tenantService.updateNameSpace(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/namespace/delete/{namespaceId}")
    public Result deleteNameSpace(@PathVariable("namespaceId") String namespaceId) {
        tenantService.deleteNameSpaceById(namespaceId);
        return Results.success();
    }
}
