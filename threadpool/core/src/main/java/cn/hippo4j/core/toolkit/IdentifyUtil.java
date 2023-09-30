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

import cn.hippo4j.common.propertie.IdentifyProperties;
import cn.hippo4j.core.api.ClientNetworkService;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.IdUtil;
import cn.hippo4j.common.toolkit.Joiner;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;
import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;

/**
 * Identify util.
 */
public class IdentifyUtil extends IdentifyProperties {

    static {
        ServiceLoaderRegistry.register(ClientNetworkService.class);
    }

    /**
     * Client identification value
     */
    public static final String CLIENT_IDENTIFICATION_VALUE = IdUtil.simpleUUID();

    /**
     * Get identify sleep time
     */
    private static final int SLEEP_TIME = 500;

    /**
     * Generate identify.
     *
     * @param environment environment
     * @param inetUtil    inet util
     * @return identify
     */
    public static synchronized String generate(ConfigurableEnvironment environment, InetUtils inetUtil) {
        if (StringUtil.isNotBlank(IDENTIFY)) {
            return IDENTIFY;
        }
        String[] customerNetwork = ServiceLoaderRegistry.getSingletonServiceInstances(ClientNetworkService.class)
                .stream().findFirst().map(each -> each.getNetworkIpPort(environment)).orElse(null);
        String ip;
        String port;
        if (customerNetwork != null && customerNetwork.length > 0) {
            ip = customerNetwork[0];
            port = customerNetwork[1];
        } else {
            ip = inetUtil.findFirstNonLoopBackHostInfo().getIpAddress();
            port = environment.getProperty("server.port", "8080");
        }
        String identify = ip
                + ":"
                + port
                + IDENTIFY_SLICER_SYMBOL
                + CLIENT_IDENTIFICATION_VALUE;
        IDENTIFY = identify;
        return identify;
    }

    /**
     * Get identify.
     *
     * @return identify
     */
    public static String getIdentify() {
        while (StringUtil.isBlank(IDENTIFY)) {
            ConfigurableEnvironment environment = ApplicationContextHolder.getBean(ConfigurableEnvironment.class);
            InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);
            if (environment != null && inetUtils != null) {
                String identify = generate(environment, inetUtils);
                return identify;
            }
            ThreadUtil.sleep(SLEEP_TIME);
        }
        return IDENTIFY;
    }

    /**
     * Get thread pool identify.
     *
     * @param threadPoolId thread pool id
     * @param itemId       item id
     * @param namespace    namespace
     * @return identify
     */
    public static String getThreadPoolIdentify(String threadPoolId, String itemId, String namespace) {
        ArrayList<String> params = CollectionUtil.newArrayList(threadPoolId, itemId, namespace, getIdentify());
        return Joiner.on(GROUP_KEY_DELIMITER).join(params);
    }
}
