package cn.hippo4j.starter.toolkit;

import cn.hippo4j.starter.toolkit.inet.InetUtils;
import lombok.SneakyThrows;
import org.springframework.core.env.PropertyResolver;

/**
 * Cloud common id util.
 *
 * @author chen.ma
 * @date 2021/8/6 21:02
 */
public class CloudCommonIdUtil {

    private static final String SEPARATOR = ":";

    @SneakyThrows
    public static String getDefaultInstanceId(PropertyResolver resolver, InetUtils inetUtils) {
        String namePart = getIpApplicationName(resolver, inetUtils);
        String indexPart = resolver.getProperty("spring.application.instance_id", resolver.getProperty("server.port"));
        return combineParts(namePart, SEPARATOR, indexPart);
    }

    @SneakyThrows
    public static String getIpApplicationName(PropertyResolver resolver, InetUtils inetUtils) {
        String hostname = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String appName = resolver.getProperty("spring.application.name");
        return combineParts(hostname, SEPARATOR, appName);
    }

    public static String combineParts(String firstPart, String separator,
                                      String secondPart) {
        String combined = null;
        if (firstPart != null && secondPart != null) {
            combined = firstPart + separator + secondPart;
        } else if (firstPart != null) {
            combined = firstPart;
        } else if (secondPart != null) {
            combined = secondPart;
        }
        return combined;
    }

}
