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
import cn.hippo4j.config.model.biz.notify.NotifyListRespDTO;
import cn.hippo4j.config.model.biz.notify.NotifyQueryReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyRespDTO;
import cn.hippo4j.config.service.biz.NotifyService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Notify controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/notify")
public class NotifyController {

    private final NotifyService notifyService;

    @PostMapping("/list/config")
    public Result<List<NotifyListRespDTO>> listNotifyConfig(@RequestBody NotifyQueryReqDTO reqDTO) {
        List<NotifyListRespDTO> resultData = notifyService.listNotifyConfig(reqDTO);
        return Results.success(resultData);
    }

    @PostMapping("/query/page")
    public Result<IPage<NotifyRespDTO>> queryPage(@RequestBody NotifyQueryReqDTO reqDTO) {
        IPage<NotifyRespDTO> resultPage = notifyService.queryPage(reqDTO);
        return Results.success(resultPage);
    }

    @PostMapping("/save")
    public Result<Void> saveNotifyConfig(@RequestBody NotifyReqDTO reqDTO) {
        notifyService.save(reqDTO);
        return Results.success();
    }

    @PostMapping("/update")
    public Result<Void> updateNotifyConfig(@RequestBody NotifyReqDTO reqDTO) {
        notifyService.update(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/remove")
    public Result<Void> removeNotifyConfig(@RequestBody NotifyReqDTO reqDTO) {
        notifyService.delete(reqDTO);
        return Results.success();
    }

    @PostMapping("/enable/{id}/{status}")
    public Result enableNotify(@PathVariable("id") String id, @PathVariable("status") Integer status) {
        notifyService.enableNotify(id, status);
        return Results.success();
    }
}
