package com.github.dynamic.threadpool.starter.toolkit;

import org.springframework.core.env.PropertyResolver;

/**
 * Cloud Common Id Util.
 *
 * @author chen.ma
 * @date 2021/8/6 21:02
 */
public class CloudCommonIdUtil {

    private static final String SEPARATOR = ":";

    public static String getDefaultInstanceId(PropertyResolver resolver) {
        String hostname = resolver.getProperty("spring.cloud.client.hostname");
        String appName = resolver.getProperty("spring.application.name");
        String namePart = combineParts(hostname, SEPARATOR, appName);
        String indexPart = resolver.getProperty("spring.application.instance_id", resolver.getProperty("server.port"));
        return combineParts(namePart, SEPARATOR, indexPart);
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
