package cn.hippo4j.config.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifyVerifyReqDTO;
import cn.hippo4j.config.service.biz.ConfigModifyVerifyService;
import cn.hippo4j.config.verify.ConfigModifyVerifyServiceChoose;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(Constants.VERIFY_PATH)
public class ConfigVerifyController {

    private final ConfigModifyVerifyServiceChoose serviceChoose;

    @PostMapping
    public Result<Void> verifyConfigModification(@RequestBody ConfigModifyVerifyReqDTO reqDTO){
        ConfigModifyVerifyService modifyVerifyService = serviceChoose.choose(reqDTO.getType());
        if (reqDTO.getAccept()) {
            modifyVerifyService.acceptModification(reqDTO.getId(),reqDTO.getThreadPoolParameterInfo());
        } else {
            modifyVerifyService.rejectModification(reqDTO.getId(),reqDTO.getThreadPoolParameterInfo());
        }
        return Results.success();
    }
}
