package cn.hippo4j.starter.controller;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.core.executor.web.WebThreadPoolHandlerChoose;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Web thread pool controller.
 *
 * <p> At present, only Tomcat is well supported, and other web containers need to be improved.
 *
 * @author chen.ma
 * @date 2022/1/19 20:54
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class WebThreadPoolController {

    private final WebThreadPoolHandlerChoose webThreadPoolServiceChoose;

    @GetMapping("/web/base/info")
    public Result<PoolBaseInfo> getPoolBaseState() {
        PoolBaseInfo poolBaseInfo = webThreadPoolServiceChoose.choose().simpleInfo();
        return Results.success(poolBaseInfo);
    }

    @GetMapping("/web/run/state")
    public Result<PoolRunStateInfo> getPoolRunState() {
        PoolRunStateInfo poolRunState = webThreadPoolServiceChoose.choose().getWebRunStateInfo();
        return Results.success(poolRunState);
    }

    @PostMapping("/web/update/pool")
    public Result<Void> updateWebThreadPool(@RequestBody PoolParameterInfo poolParameterInfo) {
        webThreadPoolServiceChoose.choose().updateWebThreadPool(poolParameterInfo);
        return Results.success();
    }

}
