package cn.hippo4j.starter.controller;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.starter.handler.ThreadPoolRunStateHandler;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/run/state/{tpId}")
    public Result<PoolRunStateInfo> getPoolRunState(@PathVariable("tpId") String tpId) {
        PoolRunStateInfo poolRunState = threadPoolRunStateHandler.getPoolRunState(tpId);
        return Results.success(poolRunState);
    }

}
