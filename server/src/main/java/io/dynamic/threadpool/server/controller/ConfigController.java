package io.dynamic.threadpool.server.controller;

import io.dynamic.threadpool.server.constant.Constants;
import io.dynamic.threadpool.server.model.ConfigInfoBase;
import io.dynamic.threadpool.server.service.ConfigService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

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

    @GetMapping
    public ConfigInfoBase detailConfigInfo(
            @RequestParam("tdId") String tdId,
            @RequestParam("itemId") String itemId,
            @RequestParam(value = "tenant", required = false, defaultValue = "") String tenant) {

        return configService.findConfigAllInfo(tdId, itemId, tenant);
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
    }

}
