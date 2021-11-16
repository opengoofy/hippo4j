package cn.hippo4j.starter.toolkit;

import lombok.SneakyThrows;
import org.springframework.core.env.PropertyResolver;

import java.net.InetAddress;

/**
 * Cloud common id util.
 *
 * @author chen.ma
 * @date 2021/8/6 21:02
 */
public class CloudCommonIdUtil {

    private static final String SEPARATOR = ":";

    @SneakyThrows
    public static String getDefaultInstanceId(PropertyResolver resolver) {
        String namePart = getIpApplicationName(resolver);
        String indexPart = resolver.getProperty("spring.application.instance_id", resolver.getProperty("server.port"));
        return combineParts(namePart, SEPARATOR, indexPart);
    }

    @SneakyThrows
    public static String getIpApplicationName(PropertyResolver resolver) {
        InetAddress host = InetAddress.getLocalHost();
        String hostname = host.getHostAddress();
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
