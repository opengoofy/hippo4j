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

import cn.hippo4j.core.springboot.starter.refresher.ApolloRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.NacosCloudRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.NacosRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.ZookeeperRefresherHandler;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config handler configuration.
 */
@Configuration(proxyBeanMethods = false)
public class ConfigHandlerConfiguration {

    private static final String NACOS_CONFIG_MANAGER_KEY = "com.alibaba.cloud.nacos.NacosConfigManager";

    private static final String NACOS_DATA_ID_KEY = "nacos.data-id";

    private static final String APOLLO_NAMESPACE_KEY = "apollo.namespace";

    private static final String ZOOKEEPER_CONNECT_STR_KEY = "zookeeper.zk-connect-str";

    @RequiredArgsConstructor
    @ConditionalOnClass(ConfigService.class)
    @ConditionalOnMissingClass(NACOS_CONFIG_MANAGER_KEY)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = NACOS_DATA_ID_KEY)
    static class EmbeddedNacos {

        public final BootstrapCoreProperties bootstrapCoreProperties;

        @Bean
        public NacosRefresherHandler nacosRefresherHandler() {
            return new NacosRefresherHandler(bootstrapCoreProperties);
        }
    }

    @ConditionalOnClass(NacosConfigManager.class)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = NACOS_DATA_ID_KEY)
    static class EmbeddedNacosCloud {

        @Bean
        public NacosCloudRefresherHandler nacosCloudRefresherHandler() {
            return new NacosCloudRefresherHandler();
        }
    }

    @ConditionalOnClass(com.ctrip.framework.apollo.ConfigService.class)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = APOLLO_NAMESPACE_KEY)
    static class EmbeddedApollo {

        @Bean
        public ApolloRefresherHandler apolloRefresher() {
            return new ApolloRefresherHandler();
        }
    }

    @ConditionalOnClass(CuratorFramework.class)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = ZOOKEEPER_CONNECT_STR_KEY)
    static class EmbeddedZookeeper {

        @Bean
        public ZookeeperRefresherHandler zookeeperRefresher() {
            return new ZookeeperRefresherHandler();
        }
    }
}
