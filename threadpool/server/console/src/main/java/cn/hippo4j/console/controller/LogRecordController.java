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
import cn.hippo4j.common.model.Result;
import cn.hippo4j.server.common.base.Results;
import cn.hippo4j.config.model.biz.log.LogRecordQueryReqDTO;
import cn.hippo4j.config.model.biz.log.LogRecordRespDTO;
import cn.hippo4j.config.service.biz.OperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Log record controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/log")
public class LogRecordController {

    private final OperationLogService operationLogService;

    @PostMapping("/query/page")
    public Result<IPage<LogRecordRespDTO>> queryPage(@RequestBody LogRecordQueryReqDTO queryReqDTO) {
        return Results.success(operationLogService.queryPage(queryReqDTO));
    }
}
