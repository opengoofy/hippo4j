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

package cn.hippo4j.springboot.starter.controller;

import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.model.ThreadDetailStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Web thread-pool run state controller.
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class WebThreadPoolRunStateController {

    private final ThreadPoolRunStateHandler threadPoolRunStateHandler;
    private final ThreadDetailState threadDetailState;

    @GetMapping("/run/state/{threadPoolId}")
    public Result<ThreadPoolRunStateInfo> getPoolRunState(@PathVariable("threadPoolId") String threadPoolId) {
        ThreadPoolRunStateInfo result = threadPoolRunStateHandler.getPoolRunState(threadPoolId);
        return Results.success(result);
    }

    @GetMapping("/run/thread/state/{threadPoolId}")
    public Result<List<ThreadDetailStateInfo>> getThreadStateDetail(@PathVariable("threadPoolId") String threadPoolId) {
        List<ThreadDetailStateInfo> result = threadDetailState.getThreadDetailStateInfo(threadPoolId);
        return Results.success(result);
    }
}
