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

package cn.hippo4j.core.enable;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.config.ConfigEmptyException;
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Objects;

/**
 * Before check configuration.
 */
@Configuration
@AllArgsConstructor
public class BeforeCheckConfiguration {

    private final String bootstrapPropertiesClassName = "cn.hippo4j.springboot.starter.config.BootstrapProperties";

    @Bean
    public BeforeCheckConfiguration.BeforeCheck dynamicThreadPoolBeforeCheckBean(@Autowired(required = false) BootstrapPropertiesInterface properties,
                                                                                 ConfigurableEnvironment environment) {
        // TODO test
        boolean checkFlag = properties != null && Objects.equals(bootstrapPropertiesClassName, properties.getClass().getName()) && properties.getEnable();
        if (checkFlag) {
            String namespace = properties.getNamespace();
            if (StringUtil.isBlank(namespace)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool namespace is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.namespace] configuration is empty or an empty string.");
            }
            String itemId = properties.getItemId();
            if (StringUtil.isBlank(itemId)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool item id is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.item-id] configuration is empty or an empty string.");
            }
            String serverAddr = properties.getServerAddr();
            if (StringUtil.isBlank(serverAddr)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool server addr is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.server-addr] configuration is empty or an empty string.");
            }
            String applicationName = environment.getProperty("spring.application.name");
            if (StringUtil.isBlank(applicationName)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool application name is empty.",
                        "Please check whether the [spring.application.name] configuration is empty or an empty string.");
            }
        }
        return new BeforeCheckConfiguration.BeforeCheck();
    }

    /**
     * Before check.
     */
    public class BeforeCheck {

    }
}
