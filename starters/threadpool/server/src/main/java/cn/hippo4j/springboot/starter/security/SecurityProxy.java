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

package cn.hippo4j.springboot.starter.security;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.common.model.TokenInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.http.HttpUtil;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Security proxy.
 */
@Slf4j
public class SecurityProxy {

    private static final String APPLY_TOKEN_URL = Constants.BASE_PATH + "/auth/users/apply/token";

    private final int refreshWindowDuration = 10;

    private final String username;

    private final String password;

    private String accessToken;

    private long tokenTtl;

    private long lastRefreshTime;

    private long tokenRefreshWindow;

    public SecurityProxy(BootstrapProperties properties) {
        username = properties.getUsername();
        password = properties.getPassword();
    }

    public boolean applyToken(List<String> servers) {
        try {
            if ((System.currentTimeMillis() - lastRefreshTime) < TimeUnit.SECONDS.toMillis(tokenTtl - tokenRefreshWindow)) {
                return true;
            }
            for (String server : servers) {
                if (applyToken(server)) {
                    lastRefreshTime = System.currentTimeMillis();
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public boolean applyToken(String server) {
        if (StringUtil.isAllNotEmpty(username, password)) {
            String url = server + APPLY_TOKEN_URL;
            Map<String, String> bodyMap = new HashMap(2);
            bodyMap.put("userName", username);
            bodyMap.put("password", password);
            try {
                Result result = HttpUtil.post(url, bodyMap, Result.class);
                if (!result.isSuccess()) {
                    log.error("Error getting access token. message: {}", result.getMessage());
                    return false;
                }
                String tokenJsonStr = JSONUtil.toJSONString(result.getData());
                TokenInfo tokenInfo = JSONUtil.parseObject(tokenJsonStr, TokenInfo.class);
                accessToken = tokenInfo.getAccessToken();
                tokenTtl = tokenInfo.getTokenTtl();
                tokenRefreshWindow = tokenTtl / refreshWindowDuration;
            } catch (Throwable ex) {
                log.error("Failed to apply for token. message: {}", ex.getMessage());
                return false;
            }
        }
        return true;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
