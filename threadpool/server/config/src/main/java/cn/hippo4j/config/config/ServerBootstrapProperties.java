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

package cn.hippo4j.config.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Server bootstrap properties.
 */
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = ServerBootstrapProperties.PREFIX)
public class ServerBootstrapProperties {

    public static final String PREFIX = "hippo4j.core";

    /**
     * Whether to start the background task of cleaning up thread pool history data.
     */
    private Boolean cleanHistoryDataEnable = Boolean.TRUE;

    /**
     * Regularly clean up the historical running data of thread pool. unit: minute.
     */
    private Integer cleanHistoryDataPeriod = 30;

    /**
     * Netty server port.
     */
    private String nettyServerPort = "8899";
}
