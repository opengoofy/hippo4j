package cn.hippo4j.discovery.controller;

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.common.web.exception.ErrorCodeEnum;
import cn.hippo4j.discovery.core.InstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.hippo4j.common.constant.Constants.BASE_PATH;

/**
 * Application controller.
 *
 * @author chen.ma
 * @date 2021/8/8 22:24
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(BASE_PATH + "/apps")
public class ApplicationController {

    private final InstanceRegistry instanceRegistry;

    @GetMapping("/{appName}")
    public Result applications(@PathVariable String appName) {
        List<Lease<InstanceInfo>> resultInstanceList = instanceRegistry.listInstance(appName);
        return Results.success(resultInstanceList);
    }

    @PostMapping("/register")
    public Result addInstance(@RequestBody InstanceInfo instanceInfo) {
        instanceRegistry.register(instanceInfo);
        return Results.success();
    }

    @PostMapping("/renew")
    public Result renew(@RequestBody InstanceInfo.InstanceRenew instanceRenew) {
        boolean isSuccess = instanceRegistry.renew(instanceRenew);
        if (!isSuccess) {
            log.warn("Not Found (Renew) :: {} - {}", instanceRenew.getAppName(), instanceRenew.getInstanceId());
            return Results.failure(ErrorCodeEnum.NOT_FOUND);
        }
        return Results.success();
    }

    @PostMapping("/remove")
    public Result remove(@RequestBody InstanceInfo instanceInfo) {
        instanceRegistry.remove(instanceInfo);
        return Results.success();
    }

}
