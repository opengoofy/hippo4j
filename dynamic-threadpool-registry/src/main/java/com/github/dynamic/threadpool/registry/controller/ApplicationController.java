package com.github.dynamic.threadpool.registry.controller;

import com.github.dynamic.threadpool.common.model.InstanceInfo;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import com.github.dynamic.threadpool.common.web.exception.ErrorCodeEnum;
import com.github.dynamic.threadpool.registry.core.InstanceRegistry;
import com.github.dynamic.threadpool.registry.core.Lease;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.github.dynamic.threadpool.common.constant.Constants.BASE_PATH;

/**
 * Application Controller.
 *
 * @author chen.ma
 * @date 2021/8/8 22:24
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH + "/apps")
public class ApplicationController {

    @NonNull
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

}
