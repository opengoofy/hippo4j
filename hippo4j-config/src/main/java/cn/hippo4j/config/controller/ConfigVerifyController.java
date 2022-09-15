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

package cn.hippo4j.config.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.toolkit.ConditionUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifyVerifyReqDTO;
import cn.hippo4j.config.service.biz.ConfigModifyVerifyService;
import cn.hippo4j.config.verify.ConfigModifyVerifyServiceChoose;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(Constants.VERIFY_PATH)
public class ConfigVerifyController {

    private final ConfigModifyVerifyServiceChoose serviceChoose;

    @PostMapping
    public Result<Void> verifyConfigModification(@RequestBody ConfigModifyVerifyReqDTO reqDTO) {
        ConfigModifyVerifyService modifyVerifyService = serviceChoose.choose(reqDTO.getType());
        ConditionUtil
                .condition(reqDTO.getAccept(),
                        () -> modifyVerifyService.acceptModification(reqDTO.getId(), reqDTO.getThreadPoolParameterInfo()),
                        () -> modifyVerifyService.rejectModification(reqDTO.getId(), reqDTO.getThreadPoolParameterInfo()));
        return Results.success();
    }
}
