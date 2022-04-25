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
