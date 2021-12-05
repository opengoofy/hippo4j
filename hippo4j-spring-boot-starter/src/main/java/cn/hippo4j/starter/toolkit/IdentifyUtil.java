package cn.hippo4j.starter.toolkit;

import cn.hippo4j.starter.toolkit.inet.InetUtils;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.env.ConfigurableEnvironment;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;
import static cn.hippo4j.starter.config.DynamicThreadPoolAutoConfiguration.CLIENT_IDENTIFICATION_VALUE;

/**
 * Identify util.
 *
 * @author chen.ma
 * @date 2021/12/5 22:25
 */
public class IdentifyUtil {

    /**
     * Generate identify.
     *
     * @param environment
     * @param hippo4JInetUtils
     * @return
     */
    public static String generate(ConfigurableEnvironment environment, InetUtils hippo4JInetUtils) {
        String ip = hippo4JInetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String port = environment.getProperty("server.port");
        String identification = StrUtil.builder(ip,
                ":",
                port,
                IDENTIFY_SLICER_SYMBOL,
                CLIENT_IDENTIFICATION_VALUE
        ).toString();

        return identification;
    }

}
