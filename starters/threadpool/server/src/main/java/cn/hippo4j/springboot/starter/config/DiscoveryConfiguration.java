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

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.springboot.starter.core.ClientShutdown;
import cn.hippo4j.springboot.starter.core.DiscoveryClient;
import cn.hippo4j.springboot.starter.provider.InstanceInfoProviderFactory;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Dynamic thread-pool discovery config.
 */
@AllArgsConstructor
public class DiscoveryConfiguration {

    private final ConfigurableEnvironment environment;

    private final BootstrapProperties bootstrapProperties;

    private final InetUtils hippo4jInetUtils;

    @Bean
    public InstanceInfo instanceConfig() {
        return InstanceInfoProviderFactory.getInstance(environment, bootstrapProperties, hippo4jInetUtils);
    }

    @Bean
    public ClientShutdown hippo4jClientShutdown() {
        return new ClientShutdown();
    }

    @Bean
    public DiscoveryClient hippo4jDiscoveryClient(HttpAgent httpAgent,
                                                  InstanceInfo instanceInfo,
                                                  ClientShutdown hippo4jClientShutdown) {
        return new DiscoveryClient(httpAgent, instanceInfo, hippo4jClientShutdown);
    }
}
