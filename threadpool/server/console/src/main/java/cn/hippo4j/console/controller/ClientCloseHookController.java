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

import cn.hippo4j.common.api.ClientCloseHookExecute;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.server.common.base.Results;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Client close hook controller.
 */
@RestController
@RequestMapping(Constants.BASE_PATH + "/client/close")
public class ClientCloseHookController {

    @PostMapping
    public Result clientCloseHook(@RequestBody ClientCloseHookExecute.ClientCloseHookReq requestParam) {
        Map<String, ClientCloseHookExecute> clientCloseHookExecuteMap = ApplicationContextHolder.getBeansOfType(ClientCloseHookExecute.class);
        clientCloseHookExecuteMap.forEach((key, execute) -> execute.closeHook(requestParam));
        return Results.success();
    }
}
