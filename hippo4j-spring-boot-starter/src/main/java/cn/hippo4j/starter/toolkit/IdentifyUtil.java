package cn.hippo4j.starter.toolkit;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.toolkit.inet.InetUtils;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;
import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;
import static cn.hippo4j.starter.config.DynamicThreadPoolAutoConfiguration.CLIENT_IDENTIFICATION_VALUE;

/**
 * Identify util.
 *
 * @author chen.ma
 * @date 2021/12/5 22:25
 */
public class IdentifyUtil {

    private static String identify;

    /**
     * Generate identify.
     *
     * @param environment
     * @param hippo4JInetUtils
     * @return
     */
    public static synchronized String generate(ConfigurableEnvironment environment, InetUtils hippo4JInetUtils) {
        if (StrUtil.isNotBlank(identify)) {
            return identify;
        }
        String ip = hippo4JInetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String port = environment.getProperty("server.port");
        String identification = StrUtil.builder(ip,
                ":",
                port,
                IDENTIFY_SLICER_SYMBOL,
                CLIENT_IDENTIFICATION_VALUE
        ).toString();

        identify = identification;
        return identification;
    }

    /**
     * Get identify.
     *
     * @return
     */
    @SneakyThrows
    public static String getIdentify() {
        while (StrUtil.isBlank(identify)) {
            ConfigurableEnvironment environment = ApplicationContextHolder.getBean(ConfigurableEnvironment.class);
            InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);

            if (environment != null && inetUtils != null) {
                String identify = generate(environment, inetUtils);
                return identify;
            }

            Thread.sleep(500);
        }

        return identify;
    }

    /**
     * Get thread pool identify.
     *
     * @param threadPoolId
     * @param properties
     * @return
     */
    public static String getThreadPoolIdentify(String threadPoolId, BootstrapProperties properties) {
        ArrayList<String> params = Lists.newArrayList(
                threadPoolId, properties.getItemId(), properties.getNamespace(), getIdentify()
        );

        return Joiner.on(GROUP_KEY_DELIMITER).join(params);
    }

}
