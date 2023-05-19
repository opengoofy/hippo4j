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
import cn.hippo4j.console.model.ChartInfo;
import cn.hippo4j.console.model.LineChartInfo;
import cn.hippo4j.console.model.PieChartInfo;
import cn.hippo4j.console.model.RankingChart;
import cn.hippo4j.console.model.TenantChart;
import cn.hippo4j.console.service.DashboardService;
import cn.hippo4j.server.common.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public Result<ChartInfo> dashboard() {
        return Results.success(dashboardService.getChartInfo());
    }

    @GetMapping("/line/chart")
    public Result<LineChartInfo> lineChart() {
        LineChartInfo lineChatInfo = dashboardService.getLineChatInfo();
        return Results.success(lineChatInfo);
    }

    @GetMapping("/tenant/chart")
    public Result<TenantChart> tenantChart() {
        TenantChart tenantChart = dashboardService.getTenantChart();
        return Results.success(tenantChart);
    }

    @GetMapping("/pie/chart")
    public Result<PieChartInfo> pieChart() {
        PieChartInfo pieChartInfo = dashboardService.getPieChart();
        return Results.success(pieChartInfo);
    }

    @GetMapping("/ranking")
    public Result<RankingChart> rankingChart() {
        RankingChart rankingChart = dashboardService.getRankingChart();
        return Results.success(rankingChart);
    }
}
