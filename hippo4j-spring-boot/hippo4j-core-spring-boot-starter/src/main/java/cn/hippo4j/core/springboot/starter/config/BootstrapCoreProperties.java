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

package cn.hippo4j.core.springboot.starter.config;

import cn.hippo4j.core.config.BootstrapPropertiesInterface;
import cn.hippo4j.core.springboot.starter.parser.ConfigFileTypeEnum;
import com.example.monitor.base.DynamicThreadPoolMonitor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Bootstrap properties.
 *
 * @author chen.ma
 * @date 2022/2/25 00:35
 */
@Getter
@Setter
@ConfigurationProperties(prefix = BootstrapCoreProperties.PREFIX)
public class BootstrapCoreProperties implements BootstrapPropertiesInterface {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * Enable dynamic thread pool.
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Enabled banner.
     */
    private Boolean banner = Boolean.TRUE;

    /***
     * Collect thread pool runtime indicators.
     */
    private Boolean collect = Boolean.TRUE;

    /**
     * Type of collection thread pool running data. eg: log,prometheus. Multiple can be used at the same time.
     * Custom SPI support {@link DynamicThreadPoolMonitor}.
     */
    private String collectType;

    /**
     * Delay starting data acquisition task. unit: ms
     */
    private Long initialDelay = 10000L;

    /**
     * Collect interval. unit: ms
     */
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
     * Whether to enable thread pool running alarm.
     */
    private Boolean alarm = Boolean.TRUE;

    /**
     * Check thread pool running status interval.
     */
    private Integer checkStateInterval;

    /**
     * Active alarm.
     */
    private Integer activeAlarm;

    /**
     * Capacity alarm.
     */
    private Integer capacityAlarm;

    /**
     * Thread pool run alarm interval. unit: s
     */
    private Integer alarmInterval;

    /**
     * Receive.
     */
    private String receive;

    /**
     * Executors.
     */
    private List<ExecutorProperties> executors;

    /**
     * Adapter executors
     */
    private List<AdapterExecutorProperties> adapterExecutors;
}
