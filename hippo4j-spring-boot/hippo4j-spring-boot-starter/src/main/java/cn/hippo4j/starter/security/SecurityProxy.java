package cn.hippo4j.starter.security;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.TokenInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.toolkit.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Security proxy.
 *
 * @author chen.ma
 * @date 2021/12/20 20:19
 */
@Slf4j
public class SecurityProxy {

    private static final String APPLY_TOKEN_URL = Constants.BASE_PATH + "/auth/users/apply/token";

    private final HttpClientUtil httpClientUtil;

    private final String username;

    private final String password;

    private String accessToken;

    private long tokenTtl;

    private long lastRefreshTime;

    private long tokenRefreshWindow;

    public SecurityProxy(HttpClientUtil httpClientUtil, BootstrapProperties properties) {
        username = properties.getUsername();
        password = properties.getPassword();
        this.httpClientUtil = httpClientUtil;
    }

    public boolean applyToken(List<String> servers) {
        try {
            if ((System.currentTimeMillis() - lastRefreshTime) < TimeUnit.SECONDS
                    .toMillis(tokenTtl - tokenRefreshWindow)) {
                return true;
            }

            for (String server : servers) {
                if (applyToken(server)) {
                    lastRefreshTime = System.currentTimeMillis();
                    return true;
                }
            }
        } catch (Throwable ignore) {
            // ignore
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
                Result<String> result = httpClientUtil.restApiPost(url, bodyMap, Result.class);
                if (!result.isSuccess()) {
                    log.error("Error getting access token. message :: {}", result.getMessage());
                    return false;
                }

                String tokenJsonStr = JSONUtil.toJSONString(result.getData());
                TokenInfo tokenInfo = JSONUtil.parseObject(tokenJsonStr, TokenInfo.class);

                accessToken = tokenInfo.getAccessToken();
                tokenTtl = tokenInfo.getTokenTtl();
                tokenRefreshWindow = tokenTtl / 10;
            } catch (Throwable ex) {
                log.error("Failed to apply for token. message :: {}", ex.getMessage());
                return false;
            }
        }

        return true;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
