package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.hippo4j.common.constant.Constants.UP;

/**
 * Health check controller.
 *
 * @author chen.ma
 * @date 2021/12/8 21:02
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/health/check")
public class HealthCheckController {

    @GetMapping
    public Result<String> healthCheck() {
        return Results.success(UP);
    }

}
