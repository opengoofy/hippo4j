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

package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bootstrap properties.
 */
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = Constants.CONFIGURATION_PROPERTIES_PREFIX)
public class BootstrapProperties implements BootstrapPropertiesInterface {

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * Server address
     */
    private String serverAddr;

    /**
     * Netty server port
     */
    private String nettyServerPort;

    /**
     * Report type
     */
    private String reportType;

    /**
     * Namespace
     */
    private String namespace;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Whether to enable dynamic thread pool
     */
    private Boolean enable = true;

    /**
     * Print dynamic thread pool banner
     */
    private Boolean banner = true;

    /**
     * Thread pool monitoring related configuration.
     */
    private MonitorProperties monitor = new MonitorProperties();

    /***
     * Latest use {@link MonitorProperties#getEnable()}
     */
    @Deprecated
    private Boolean collect = Boolean.TRUE;

    /**
     * Latest use {@link MonitorProperties#getCollectTypes()}
     */
    @Deprecated
    private String collectType;

    /**
     * Latest use {@link MonitorProperties#getInitialDelay()}
     */
    @Deprecated
    private Long initialDelay = 10000L;

    /**
     * Latest use {@link MonitorProperties#getCollectInterval()}
     */
    @Deprecated
    private Long collectInterval = 5000L;

    /**
     * Latest use {@link MonitorProperties#getTaskBufferSize()}
     */
    @Deprecated
    private Integer taskBufferSize = 4096;
}
