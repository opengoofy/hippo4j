package io.dynamic.threadpool.server.controller;

import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.web.base.Result;
import io.dynamic.threadpool.common.web.base.Results;
import io.dynamic.threadpool.server.model.ConfigAllInfo;
import io.dynamic.threadpool.server.model.ConfigInfoBase;
import io.dynamic.threadpool.server.service.biz.ConfigService;
import io.dynamic.threadpool.server.service.ConfigServletInner;
import io.dynamic.threadpool.server.toolkit.Md5ConfigUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 服务端配置控制器
 *
 * @author chen.ma
 * @date 2021/6/20 13:53
 */
@RestController
@RequestMapping(Constants.CONFIG_CONTROLLER_PATH)
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigServletInner configServletInner;

    @GetMapping
    public Result<ConfigInfoBase> detailConfigInfo(
            @RequestParam("tpId") String tpId,
            @RequestParam("itemId") String itemId,
            @RequestParam(value = "namespace") String namespace) {

        return Results.success(configService.findConfigAllInfo(tpId, itemId, namespace));
    }

    @PostMapping
    public Result<Boolean> publishConfig(@RequestBody ConfigAllInfo config) {
        configService.insertOrUpdate(config);
        return Results.success(true);
    }

    @SneakyThrows
    @PostMapping("/listener")
    public void listener(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);

        String probeModify = request.getParameter(Constants.LISTENING_CONFIGS);
        if (StringUtils.isEmpty(probeModify)) {
            throw new IllegalArgumentException("invalid probeModify");
        }

        probeModify = URLDecoder.decode(probeModify, Constants.ENCODE);

        Map<String, String> clientMd5Map;
        try {
            clientMd5Map = Md5ConfigUtil.getClientMd5Map(probeModify);
        } catch (Throwable e) {
            throw new IllegalArgumentException("invalid probeModify");
        }

        configServletInner.doPollingConfig(request, response, clientMd5Map, probeModify.length());
    }

}
