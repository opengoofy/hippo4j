/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.discovery.controller;

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.discovery.core.InstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import cn.hippo4j.server.common.base.Results;
import cn.hippo4j.server.common.base.exception.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.hippo4j.common.constant.Constants.BASE_PATH;

/**
 * Application controller.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(BASE_PATH + "/apps")
public class ApplicationController {

    private final InstanceRegistry<InstanceInfo> instanceRegistry;

    @GetMapping("/{appName}")
    public Result<List<Lease<InstanceInfo>>> applications(@PathVariable String appName) {
        List<Lease<InstanceInfo>> resultInstanceList = instanceRegistry.listInstance(appName);
        return Results.success(resultInstanceList);
    }

    @PostMapping("/register")
    public Result<Void> addInstance(@RequestBody InstanceInfo instanceInfo) {
        instanceRegistry.register(instanceInfo);
        return Results.success();
    }

    @PostMapping("/renew")
    public Result<Void> renew(@RequestBody InstanceInfo.InstanceRenew instanceRenew) {
        boolean isSuccess = instanceRegistry.renew(instanceRenew);
        if (!isSuccess) {
            log.warn("Not Found (Renew): {} - {}", instanceRenew.getAppName(), instanceRenew.getInstanceId());
            return Results.failure(ErrorCodeEnum.NOT_FOUND);
        }
        return Results.success();
    }

    @PostMapping("/remove")
    public Result<Void> remove(@RequestBody InstanceInfo instanceInfo) {
        instanceRegistry.remove(instanceInfo);
        return Results.success();
    }
}
