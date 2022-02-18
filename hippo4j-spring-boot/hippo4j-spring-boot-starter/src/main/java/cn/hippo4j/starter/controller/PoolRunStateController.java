package cn.hippo4j.starter.controller;

import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.model.ThreadDetailStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.starter.handler.ThreadPoolRunStateHandler;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Pool run state controller.
 *
 * @author chen.ma
 * @date 2021/7/7 21:34
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class PoolRunStateController {

    private final ThreadPoolRunStateHandler threadPoolRunStateHandler;

    private final ThreadDetailState threadDetailState;

    @GetMapping("/run/state/{threadPoolId}")
    public Result<PoolRunStateInfo> getPoolRunState(@PathVariable("threadPoolId") String threadPoolId) {
        PoolRunStateInfo poolRunState = threadPoolRunStateHandler.getPoolRunState(threadPoolId);
        return Results.success(poolRunState);
    }

    @GetMapping("/run/thread/state/{threadPoolId}")
    public Result<List<ThreadDetailStateInfo>> getThreadStateDetail(@PathVariable("threadPoolId") String threadPoolId) {
        List<ThreadDetailStateInfo> detailStateInfo = threadDetailState.getThreadDetailStateInfo(threadPoolId);
        return Results.success(detailStateInfo);
    }

}
