package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.console.model.ChartInfo;
import cn.hippo4j.console.service.DashboardService;
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
