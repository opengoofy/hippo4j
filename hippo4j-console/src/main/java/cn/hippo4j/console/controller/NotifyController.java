package cn.hippo4j.console.controller;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.config.model.biz.notify.NotifyListRespDTO;
import cn.hippo4j.config.model.biz.notify.NotifyQueryReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyRespDTO;
import cn.hippo4j.config.service.biz.NotifyService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:00
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/notify")
public class NotifyController {

    private final NotifyService notifyService;

    @PostMapping("/list/config")
    public Result<List<NotifyListRespDTO>> listNotifyConfig(@RequestBody NotifyQueryReqDTO reqDTO) {
        List<NotifyListRespDTO> resultData = notifyService.listNotifyConfig(reqDTO);
        return Results.success(resultData);
    }

    @PostMapping("/query/page")
    public Result<IPage<NotifyRespDTO>> queryPage(@RequestBody NotifyQueryReqDTO reqDTO) {
        IPage<NotifyRespDTO> resultPage = notifyService.queryPage(reqDTO);
        return Results.success(resultPage);
    }

    @PostMapping("/save")
    public Result<Void> saveNotifyConfig(@RequestBody NotifyReqDTO reqDTO) {
        notifyService.save(reqDTO);
        return Results.success();
    }

    @PostMapping("/update")
    public Result<Void> updateNotifyConfig(@RequestBody NotifyReqDTO reqDTO) {
        notifyService.update(reqDTO);
        return Results.success();
    }

    @DeleteMapping("/remove")
    public Result<Void> removeNotifyConfig(@RequestBody NotifyReqDTO reqDTO) {
        notifyService.delete(reqDTO);
        return Results.success();
    }

    @PostMapping("/enable/{id}/{status}")
    public Result enableNotify(@PathVariable("id") String id, @PathVariable("status") Integer status) {
        notifyService.enableNotify(id, status);
        return Results.success();
    }

}
