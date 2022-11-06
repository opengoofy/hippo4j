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

package cn.hippo4j.springboot.starter.toolkit;

import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.SneakyThrows;
import org.springframework.core.env.PropertyResolver;

/**
 * Cloud common id util.
 */
public class CloudCommonIdUtil {

    /**
     * Splice target information separator
     */
    private static final String SEPARATOR = ":";

    /**
     * Get client ip port.
     *
     * @param resolver  resolver
     * @param inetUtils inet utils
     * @return ip and port
     */
    public static String getClientIpPort(PropertyResolver resolver, InetUtils inetUtils) {
        String hostname = inetUtils.findFirstNonLoopBackHostInfo().getIpAddress();
        String port = resolver.getProperty("server.port", "8080");
        return combineParts(hostname, SEPARATOR, port);
    }

    /**
     * Get default instance id.
     *
     * @param resolver  resolver
     * @param inetUtils inet utils
     * @return default instance id
     */
    @SneakyThrows
    public static String getDefaultInstanceId(PropertyResolver resolver, InetUtils inetUtils) {
        String namePart = getIpApplicationName(resolver, inetUtils);
        String indexPart = resolver.getProperty("spring.application.instance_id", resolver.getProperty("server.port"));
        return combineParts(namePart, SEPARATOR, indexPart);
    }

    /**
     * Get ip application name.
     *
     * @param resolver  resolver
     * @param inetUtils inet utils
     * @return ip application name
     */
    @SneakyThrows
    public static String getIpApplicationName(PropertyResolver resolver, InetUtils inetUtils) {
        String hostname = inetUtils.findFirstNonLoopBackHostInfo().getIpAddress();
        String appName = resolver.getProperty("spring.application.name");
        return combineParts(hostname, SEPARATOR, appName);
    }

    /**
     * Combine parts.
     *
     * @param firstPart  first part
     * @param separator  separator
     * @param secondPart second part
     * @return combined
     */
    public static String combineParts(String firstPart, String separator, String secondPart) {
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
