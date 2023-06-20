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

import cn.hippo4j.config.springboot.starter.refresher.*;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.BootstrapConfigPropertiesBinderAdapter;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.tencent.polaris.configuration.api.core.ConfigFileService;
import io.etcd.jetcd.Client;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config handler configuration.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class ConfigHandlerConfiguration {

    private static final String NACOS_CONFIG_MANAGER_KEY = "com.alibaba.cloud.nacos.NacosConfigManager";

    private static final String NACOS_INJECTED_BEAN_NAME = "com.alibaba.nacos.spring.beans.factory.annotation.AnnotationNacosInjectedBeanPostProcessor";

    private static final String NACOS_DATA_ID_KEY = "nacos.data-id";

    private static final String APOLLO_NAMESPACE_KEY = "apollo.namespace";

    private static final String CONSUL_DATA_KEY = "consul.data-key";

    private static final String ZOOKEEPER_CONNECT_STR_KEY = "zookeeper.zk-connect-str";

    private static final String ETCD = "etcd.endpoints";

    private static final String POLARIS = "config.serverConnector";

    @Bean
    @ConditionalOnMissingBean
    public BootstrapConfigPropertiesBinderAdapter bootstrapConfigPropertiesBinderAdapter() {
        return new DefaultBootstrapConfigPropertiesBinderAdapter();
    }

    /**
     * Embedded Nacos
     */
    @RequiredArgsConstructor
    @ConditionalOnClass(value = ConfigService.class, name = NACOS_INJECTED_BEAN_NAME)
    @ConditionalOnMissingClass(NACOS_CONFIG_MANAGER_KEY)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = NACOS_DATA_ID_KEY)
    static class EmbeddedNacos {

        @Bean
        public NacosRefresherHandler nacosRefresherHandler() {
            return new NacosRefresherHandler();
        }
    }

    /**
     * Embedded Nacos Cloud
     */
    @ConditionalOnClass(NacosConfigProperties.class)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = NACOS_DATA_ID_KEY)
    static class EmbeddedNacosCloud {

        @Bean
        public NacosCloudRefresherHandler nacosCloudRefresherHandler() {
            return new NacosCloudRefresherHandler();
        }
    }

    /**
     * Embedded Apollo
     */
    @ConditionalOnClass(com.ctrip.framework.apollo.ConfigService.class)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = APOLLO_NAMESPACE_KEY)
    static class EmbeddedApollo {

        @Bean
        public ApolloRefresherHandler apolloRefresher() {
            return new ApolloRefresherHandler();
        }
    }

    /**
     * Embedded Consul
     */
    @ConditionalOnClass(ConsulConfigProperties.class)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = CONSUL_DATA_KEY)
    static class EmbeddedConsul {

        @Bean
        public ConsulRefresherHandler consulRefresher() {
            return new ConsulRefresherHandler();
        }
    }

    /**
     * Embedded Zookeeper
     */
    @ConditionalOnClass(CuratorFramework.class)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = ZOOKEEPER_CONNECT_STR_KEY)
    static class EmbeddedZookeeper {

        @Bean
        public ZookeeperRefresherHandler zookeeperRefresher() {
            return new ZookeeperRefresherHandler();
        }
    }

    /**
     * Embedded Etcd
     */
    @ConditionalOnClass(Client.class)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = ETCD)
    static class EmbeddedEtcd {

        @Bean
        public EtcdRefresherHandler etcdRefresher() {
            return new EtcdRefresherHandler();
        }
    }

    /**
     * Polaris
     */
    @ConditionalOnClass(ConfigFileService.class)
    @ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, name = POLARIS)
    static class Polaris {

        @Bean
        public PolarisRefresherHandler polarisRefresher(ConfigFileService configFileService) {
            return new PolarisRefresherHandler(configFileService);
        }
    }
}
