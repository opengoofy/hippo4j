package com.github.dynamic.threadpool.starter.controller;

import com.github.dynamic.threadpool.starter.handler.ThreadPoolRunStateHandler;
import com.github.dynamic.threadpool.common.model.PoolRunStateInfo;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.common.web.base.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pool Run State Controller.
 *
 * @author chen.ma
 * @date 2021/7/7 21:34
 */
@RestController
public class PoolRunStateController {

    @GetMapping("/run/state/{tpId}")
    public Result<PoolRunStateInfo> getPoolRunState(@PathVariable("tpId") String tpId) {
        PoolRunStateInfo poolRunState = ThreadPoolRunStateHandler.getPoolRunState(tpId);
        return Results.success(poolRunState);
    }
}
