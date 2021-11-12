package com.github.dynamic.threadpool.console.controller;

import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import com.github.dynamic.threadpool.console.model.ChartInfo;
import com.github.dynamic.threadpool.console.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dash board controller.
 *
 * @author chen.ma
 * @date 2021/11/10 21:03
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

}
