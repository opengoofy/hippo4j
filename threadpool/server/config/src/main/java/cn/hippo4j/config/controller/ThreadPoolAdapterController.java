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

import cn.hippo4j.adapter.base.ThreadPoolAdapterCacheConfig;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.config.service.ThreadPoolAdapterService;
import cn.hippo4j.server.common.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.hippo4j.common.constant.Constants.REGISTER_ADAPTER_PATH;

/**
 * Thread-pool adapter controller.
 */
@RestController
@AllArgsConstructor
public class ThreadPoolAdapterController {

    private final ThreadPoolAdapterService threadPoolAdapterService;

    @PostMapping(REGISTER_ADAPTER_PATH)
    public Result registerAdapterThreadPool(@RequestBody List<ThreadPoolAdapterCacheConfig> requestParameter) {
        threadPoolAdapterService.register(requestParameter);
        return Results.success();
    }
}
