package cn.hippo4j.config.controller;

import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ConfigInfoBase;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.service.ConfigServletInner;
import cn.hippo4j.config.toolkit.Md5ConfigUtil;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Server configuration controller.
 *
 * @author chen.ma
 * @date 2021/6/20 13:53
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.CONFIG_CONTROLLER_PATH)
public class ConfigController {

    private final ConfigService configService;

    private final ConfigServletInner configServletInner;

    @GetMapping
    public Result<ConfigInfoBase> detailConfigInfo(
            @RequestParam("tpId") String tpId,
            @RequestParam("itemId") String itemId,
            @RequestParam("namespace") String namespace,
            @RequestParam(value = "instanceId", required = false) String instanceId) {
        ConfigAllInfo configAllInfo = configService.findConfigRecentInfo(tpId, itemId, namespace, instanceId);
        return Results.success(configAllInfo);
    }

    @PostMapping
    public Result<Boolean> publishConfig(@RequestParam(value = "identify", required = false) String identify,
                                         @RequestBody ConfigAllInfo config) {
        configService.insertOrUpdate(identify, config);
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

    @PostMapping("/remove/config/cache")
    public Result removeConfigCache(@RequestBody Map<String, String> bodyMap) {
        String groupKey = bodyMap.get(Constants.GROUP_KEY);
        if (StrUtil.isNotBlank(groupKey)) {
            ConfigCacheService.removeConfigCache(groupKey);
        }

        return Results.success();
    }

}
