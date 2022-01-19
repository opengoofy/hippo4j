package cn.hippo4j.starter.controller;

import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.starter.handler.web.WebThreadPoolRunStateHandler;
import cn.hippo4j.starter.handler.web.WebThreadPoolService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Executor;

/**
 * Web thread pool controller.
 *
 * @author chen.ma
 * @date 2022/1/19 20:54
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class WebThreadPoolController {

    private final WebThreadPoolService webThreadPoolService;

    private final ThreadDetailState threadDetailState;

    private final WebThreadPoolRunStateHandler webThreadPoolRunStateHandler;

    @GetMapping("/web/run/state")
    public Result<PoolRunStateInfo> getPoolRunState() {
        Executor webThreadPool = webThreadPoolService.getWebThreadPool();
        PoolRunStateInfo poolRunState = webThreadPoolRunStateHandler.getPoolRunState(null, webThreadPool);
        return Results.success(poolRunState);
    }

    @PostMapping("/web/update/pool")
    public Result<Void> updateWebThreadPool(@RequestBody PoolParameterInfo poolParameterInfo) {
        webThreadPoolService.updateWebThreadPool(poolParameterInfo);
        return Results.success();
    }

}
