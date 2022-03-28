package cn.hippo4j.config.toolkit;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;

import static cn.hippo4j.common.constant.Constants.LONG_PULLING_CLIENT_IDENTIFICATION;

/**
 * Request util.
 *
 * @author chen.ma
 * @date 2021/6/23 18:28
 */
public class RequestUtil {

    private static final String X_REAL_IP = "X-Real-IP";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String X_FORWARDED_FOR_SPLIT_SYMBOL = ",";

    public static String getClientIdentify(HttpServletRequest request) {
        String identify = request.getHeader(LONG_PULLING_CLIENT_IDENTIFICATION);
        return StrUtil.isBlank(identify) ? "" : identify;
    }

}
