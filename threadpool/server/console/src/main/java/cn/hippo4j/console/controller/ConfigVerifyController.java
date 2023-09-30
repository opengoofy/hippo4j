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

package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.toolkit.ConditionUtil;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.config.model.biz.threadpool.ConfigModificationQueryRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifyVerifyReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.service.biz.ConfigModificationQueryService;
import cn.hippo4j.config.service.biz.ConfigModificationVerifyService;
import cn.hippo4j.config.verify.ConfigModificationVerifyServiceChoose;
import cn.hippo4j.server.common.base.Results;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Config Verify Controller
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.VERIFY_PATH)
public class ConfigVerifyController {

    private final ConfigModificationVerifyServiceChoose serviceChoose;

    private final ConfigModificationQueryService queryService;

    @PostMapping
    public Result<Void> verifyConfigModification(@RequestBody ConfigModifyVerifyReqDTO reqDTO) {
        ConfigModificationVerifyService modifyVerifyService = serviceChoose.choose(reqDTO.getType());
        ConditionUtil
                .condition(reqDTO.getAccept(),
                        () -> modifyVerifyService.acceptModification(reqDTO),
                        () -> modifyVerifyService.rejectModification(reqDTO));
        return Results.success();
    }

    @PostMapping("/query/application/page")
    public Result<IPage<ConfigModificationQueryRespDTO>> modificationApplicationPage(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(queryService.queryApplicationPage(reqDTO));
    }

    @GetMapping("/query/application/detail")
    public Result<ThreadPoolParameterInfo> modificationApplicationDetail(@RequestParam("id") String id) {
        return Results.success(queryService.queryApplicationDetail(Long.parseLong(id)));
    }

}
