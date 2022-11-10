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

package cn.hippo4j.config.springboot.starter.config;

import cn.hippo4j.config.springboot.starter.parser.ConfigFileTypeEnum;
import cn.hippo4j.core.config.BootstrapPropertiesInterface;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Bootstrap core properties.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = BootstrapConfigProperties.PREFIX)
public class BootstrapConfigProperties implements BootstrapPropertiesInterface {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * Enable dynamic thread pool.
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Enabled banner.
     */
    private Boolean banner = Boolean.TRUE;

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
     * Config file type.
     */
    private ConfigFileTypeEnum configFileType;

    /**
     * Nacos config.
     */
    private Map<String, String> nacos;

    /**
     * Apollo config.
     */
    private Map<String, String> apollo;

    /**
     * Zookeeper config.
     */
    private Map<String, String> zookeeper;

    /**
     * etcd config
     */
    private Map<String, String> etcd;

    /**
     * Tomcat thread pool config.
     */
    private WebThreadPoolProperties tomcat;

    /**
     * Undertow thread pool config.
     */
    private WebThreadPoolProperties undertow;

    /**
     * Jetty thread pool config.
     * KeepAliveTime is not supported temporarily.
     */
    private WebThreadPoolProperties jetty;

    /**
     * Notify platforms.
     */
    private List<NotifyPlatformProperties> notifyPlatforms;

    /**
     * Check thread pool running status interval.
     */
    private Integer checkStateInterval;

    /**
     * Default dynamic thread pool configuration.
     */
    private ExecutorProperties defaultExecutor;

    /**
     * Dynamic thread pool configuration collection.
     */
    private List<ExecutorProperties> executors;

    /**
     * Tripartite framework thread pool adaptation set.
     */
    private List<AdapterExecutorProperties> adapterExecutors;
}
