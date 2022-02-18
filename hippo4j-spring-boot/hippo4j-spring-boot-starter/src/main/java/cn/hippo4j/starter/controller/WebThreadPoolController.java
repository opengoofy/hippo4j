package cn.hippo4j.starter.controller;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.starter.handler.web.WebThreadPoolHandlerChoose;
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

    private final WebThreadPoolHandlerChoose webThreadPoolServiceChoose;

    private final WebThreadPoolRunStateHandler webThreadPoolRunStateHandler;

    @GetMapping("/web/base/info")
    public Result<PoolBaseInfo> getPoolBaseState() {
        WebThreadPoolService webThreadPoolService = webThreadPoolServiceChoose.choose();
        Executor webThreadPool = webThreadPoolService.getWebThreadPool();
        PoolBaseInfo poolBaseInfo = webThreadPoolRunStateHandler.simpleInfo(webThreadPool);
        return Results.success(poolBaseInfo);
    }

    @GetMapping("/web/run/state")
    public Result<PoolRunStateInfo> getPoolRunState() {
        WebThreadPoolService webThreadPoolService = webThreadPoolServiceChoose.choose();
        Executor webThreadPool = webThreadPoolService.getWebThreadPool();
        PoolRunStateInfo poolRunState = webThreadPoolRunStateHandler.getPoolRunState(null, webThreadPool);
        return Results.success(poolRunState);
    }

    @PostMapping("/web/update/pool")
    public Result<Void> updateWebThreadPool(@RequestBody PoolParameterInfo poolParameterInfo) {
        WebThreadPoolService webThreadPoolService = webThreadPoolServiceChoose.choose();
        webThreadPoolService.updateWebThreadPool(poolParameterInfo);
        return Results.success();
    }

}
