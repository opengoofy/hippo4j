package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.toolkit.BeanUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Monitor controller.
 *
 * @author chen.ma
 * @date 2021/12/7 22:29
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/monitor")
public class MonitorController {

    @PostMapping
    public Result dataAcquisition(@RequestBody MessageWrapper messageWrapper) {
        if (messageWrapper.getMessageType().name().equals(MessageTypeEnum.RUNTIME.name())) {
            RuntimeMessage convert = BeanUtil.convert(messageWrapper.getMessageObj(), RuntimeMessage.class);
            log.info("Receive runtime data uploaded by the client. message :: {}", JSON.toJSONString(convert));
        }
        return Results.success();
    }

}
