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

package cn.hippo4j.core.toolkit;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.*;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.UUID;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;
import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;

/**
 * Identify util.
 */
public class IdentifyUtil {

    private static String IDENTIFY;

    public static final String CLIENT_IDENTIFICATION_VALUE = IdUtil.simpleUUID();

    /**
     * Generate identify.
     *
     * @param environment
     * @param hippo4JInetUtils
     * @return
     */
    public static synchronized String generate(ConfigurableEnvironment environment, InetUtils hippo4JInetUtils) {
        if (StringUtil.isNotBlank(IDENTIFY)) {
            return IDENTIFY;
        }
        String ip = hippo4JInetUtils.findFirstNonLoopBackHostInfo().getIpAddress();
        String port = environment.getProperty("server.port", "8080");
        String identification = new StringBuilder()
                .append(ip)
                .append(":")
                .append(port)
                .append(IDENTIFY_SLICER_SYMBOL)
                .append(CLIENT_IDENTIFICATION_VALUE)
                .toString();
        IDENTIFY = identification;
        return identification;
    }

    /**
     * Get identify.
     *
     * @return
     */
    public static String getIdentify() {
        while (StringUtil.isBlank(IDENTIFY)) {
            ConfigurableEnvironment environment = ApplicationContextHolder.getBean(ConfigurableEnvironment.class);
            InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);
            if (environment != null && inetUtils != null) {
                String identify = generate(environment, inetUtils);
                return identify;
            }
            ThreadUtil.sleep(500);
        }
        return IDENTIFY;
    }

    /**
     * Get thread pool identify.
     *
     * @param threadPoolId
     * @param itemId
     * @param namespace
     * @return
     */
    public static String getThreadPoolIdentify(String threadPoolId, String itemId, String namespace) {
        ArrayList<String> params = CollectionUtil.newArrayList(threadPoolId, itemId, namespace, getIdentify());
        return Joiner.on(GROUP_KEY_DELIMITER).join(params);
    }
}
