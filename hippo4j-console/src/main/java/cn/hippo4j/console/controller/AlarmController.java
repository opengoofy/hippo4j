package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.biz.alarm.AlarmListRespDTO;
import cn.hippo4j.config.model.biz.alarm.AlarmQueryReqDTO;
import cn.hippo4j.config.service.biz.AlarmService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报警管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:00
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/list/config")
    public Result<List<AlarmListRespDTO>> listAlarmConfig(@RequestBody AlarmQueryReqDTO reqDTO) {
        List<AlarmListRespDTO> resultData = alarmService.listAlarmConfig(reqDTO);
        return Results.success(resultData);
    }

}
