package cn.hippo4j.console.controller;

import cn.hippo4j.common.api.ClientCloseHookExecute;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Client close hook controller.
 *
 * @author chen.ma
 * @date 2022/1/6 22:30
 */
@RestController
@RequestMapping(Constants.BASE_PATH + "/client/close")
public class ClientCloseHookController {

    @PostMapping
    public Result clientCloseHook(@RequestBody ClientCloseHookExecute.ClientCloseHookReq req) {
        Map<String, ClientCloseHookExecute> clientCloseHookExecuteMap = ApplicationContextHolder.getBeansOfType(ClientCloseHookExecute.class);
        clientCloseHookExecuteMap.forEach((key, execute) -> execute.closeHook(req));
        return Results.success();
    }

}
