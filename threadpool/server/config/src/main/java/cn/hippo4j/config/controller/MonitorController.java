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
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.config.model.biz.monitor.MonitorActiveRespDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorQueryReqDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorRespDTO;
import cn.hippo4j.config.monitor.QueryMonitorExecuteChoose;
import cn.hippo4j.config.service.biz.HisRunDataService;
import cn.hippo4j.server.common.base.Results;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Monitor controller.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/monitor")
public class MonitorController {

    private final HisRunDataService hisRunDataService;

    private final QueryMonitorExecuteChoose queryMonitorExecuteChoose;

    private final ThreadPoolTaskExecutor monitorThreadPoolTaskExecutor;

    @GetMapping
    public Result<List<MonitorRespDTO>> queryMonitor(MonitorQueryReqDTO reqDTO) {
        List<MonitorRespDTO> monitorRespList = hisRunDataService.query(reqDTO);
        return Results.success(monitorRespList);
    }

    @PostMapping("/info")
    public Result<MonitorActiveRespDTO> queryInfoThreadPoolMonitor(@RequestBody MonitorQueryReqDTO reqDTO) {
        MonitorActiveRespDTO monitorRespList = hisRunDataService.queryInfoThreadPoolMonitor(reqDTO);
        return Results.success(monitorRespList);
    }

    @PostMapping("/last/task/count")
    public Result<MonitorRespDTO> queryThreadPoolLastTaskCount(@RequestBody MonitorQueryReqDTO reqDTO) {
        MonitorRespDTO resultDTO = hisRunDataService.queryThreadPoolLastTaskCount(reqDTO);
        return Results.success(resultDTO);
    }

    @PostMapping
    public Result<Void> dataCollect(@RequestBody MessageWrapper messageWrapper) {
        return hisRunDataService.dataCollect(messageWrapper);
    }
}
