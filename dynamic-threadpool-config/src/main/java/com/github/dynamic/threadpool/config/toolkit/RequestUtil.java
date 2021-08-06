package com.github.dynamic.threadpool.config.toolkit;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Request Util.
 *
 * @author chen.ma
 * @date 2021/6/23 18:28
 */
public class RequestUtil {

    private static final String X_REAL_IP = "X-Real-IP";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String X_FORWARDED_FOR_SPLIT_SYMBOL = ",";

    public static String getRemoteIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (!StringUtils.isEmpty(xForwardedFor)) {
            return xForwardedFor.split(X_FORWARDED_FOR_SPLIT_SYMBOL)[0].trim();
        }
        String nginxHeader = request.getHeader(X_REAL_IP);
        return StringUtils.isEmpty(nginxHeader) ? request.getRemoteAddr() : nginxHeader;
    }
}
