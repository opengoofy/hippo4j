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

import cn.hippo4j.adapter.web.WebThreadPoolHandlerChoose;
import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.common.model.ThreadPoolBaseInfo;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web thread pool controller.
 *
 * <p> At present, only Tomcat is well supported, and other web containers need to be improved.
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class WebThreadPoolController {

    private final WebThreadPoolHandlerChoose webThreadPoolServiceChoose;

    @GetMapping("/web/base/info")
    public Result<ThreadPoolBaseInfo> getPoolBaseState(@RequestParam(value = "mark") String mark) {
        WebThreadPoolService webThreadPoolService = webThreadPoolServiceChoose.choose();
        if (webThreadPoolService != null && webThreadPoolService.getClass().getSimpleName().contains(mark)) {
            return new Result<ThreadPoolBaseInfo>().setCode(Result.SUCCESS_CODE).setData(webThreadPoolService.simpleInfo());
        }
        return new Result<ThreadPoolBaseInfo>().setCode(Result.SUCCESS_CODE);
    }

    @GetMapping("/web/run/state")
    public Result<ThreadPoolRunStateInfo> getPoolRunState() {
        return new Result<ThreadPoolRunStateInfo>().setCode(Result.SUCCESS_CODE).setData(webThreadPoolServiceChoose.choose().getWebRunStateInfo());
    }

    @PostMapping("/web/update/pool")
    public Result<Void> updateWebThreadPool(@RequestBody ThreadPoolParameterInfo threadPoolParameterInfo) {
        webThreadPoolServiceChoose.choose().updateWebThreadPool(threadPoolParameterInfo);
        return new Result<Void>().setCode(Result.SUCCESS_CODE);
    }
}
