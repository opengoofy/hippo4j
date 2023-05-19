/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.config.controller;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hippo4j.common.constant.ConfigModifyTypeConstants;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ConfigInfoBase;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifySaveReqDTO;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.service.ConfigServletInner;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.config.toolkit.Md5ConfigUtil;
import cn.hippo4j.config.verify.ConfigModificationVerifyServiceChoose;
import cn.hippo4j.server.common.base.Results;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Server configuration controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.CONFIG_CONTROLLER_PATH)
public class ConfigController {

    private final ConfigService configService;

    private final ConfigServletInner configServletInner;

    private final ConfigModificationVerifyServiceChoose configModificationVerifyServiceChoose;

    @GetMapping
    public Result<ConfigInfoBase> detailConfigInfo(@RequestParam("tpId") String tpId,
                                                   @RequestParam("itemId") String itemId,
                                                   @RequestParam("namespace") String namespace,
                                                   @RequestParam(value = "instanceId", required = false) String instanceId) {
        ConfigAllInfo configAllInfo = configService.findConfigRecentInfo(tpId, itemId, namespace, instanceId);
        return Results.success(configAllInfo);
    }

    @PostMapping
    public Result<Boolean> publishConfig(@RequestParam(value = "identify", required = false) String identify,
                                         @RequestBody ConfigAllInfo config) {
        if (UserContext.getUserRole().equals("ROLE_ADMIN")) {
            configService.insertOrUpdate(identify, true, config);
        } else {
            ConfigModifySaveReqDTO modifySaveReqDTO = BeanUtil.convert(config, ConfigModifySaveReqDTO.class);
            modifySaveReqDTO.setCorePoolSize(config.getCoreSize());
            modifySaveReqDTO.setMaximumPoolSize(config.getMaxSize());
            modifySaveReqDTO.setModifyUser(UserContext.getUserName());
            modifySaveReqDTO.setModifyAll(StringUtil.isEmpty(identify));
            modifySaveReqDTO.setIdentify(identify);
            modifySaveReqDTO.setType(ConfigModifyTypeConstants.THREAD_POOL_INSTANCE);
            configModificationVerifyServiceChoose.choose(modifySaveReqDTO.getType()).saveConfigModifyApplication(modifySaveReqDTO);
        }
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
        if (StringUtil.isNotBlank(groupKey)) {
            ConfigCacheService.removeConfigCache(groupKey);
        }
        return Results.success();
    }

    @PostMapping("/register")
    public Result register(@RequestBody DynamicThreadPoolRegisterWrapper registerWrapper) {
        configService.register(registerWrapper);
        return Results.success();
    }
}
