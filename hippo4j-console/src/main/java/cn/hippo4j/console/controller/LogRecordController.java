package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.biz.log.LogRecordQueryReqDTO;
import cn.hippo4j.config.model.biz.log.LogRecordRespDTO;
import cn.hippo4j.config.service.biz.LogRecordBizService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Log record controller.
 *
 * @author chen.ma
 * @date 2021/11/17 21:10
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/log")
public class LogRecordController {

    private final LogRecordBizService logRecordBizService;

    @PostMapping("/query/page")
    public Result<IPage<LogRecordRespDTO>> queryPage(@RequestBody LogRecordQueryReqDTO queryReqDTO) {
        return Results.success(logRecordBizService.queryPage(queryReqDTO));
    }

}
