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

import cn.hippo4j.common.api.ClientNetworkService;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.spi.DynamicThreadPoolServiceLoader;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.springboot.starter.core.DiscoveryClient;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import cn.hippo4j.springboot.starter.toolkit.CloudCommonIdUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.util.Optional;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;
import static cn.hippo4j.core.toolkit.IdentifyUtil.CLIENT_IDENTIFICATION_VALUE;

/**
 * Dynamic thread-pool discovery config.
 */
@AllArgsConstructor
public class DiscoveryConfiguration {

    private final ConfigurableEnvironment environment;

    private final BootstrapProperties bootstrapProperties;

    private final InetUtils hippo4JInetUtils;

    static {
        DynamicThreadPoolServiceLoader.register(ClientNetworkService.class);
    }

    @Bean
    @SneakyThrows
    public InstanceInfo instanceConfig() {
        String namespace = bootstrapProperties.getNamespace();
        String itemId = bootstrapProperties.getItemId();
        String port = environment.getProperty("server.port", "8080");
        String applicationName = environment.getProperty("spring.dynamic.thread-pool.item-id");
        String active = environment.getProperty("spring.profiles.active", "UNKNOWN");
        InstanceInfo instanceInfo = new InstanceInfo();
        String instanceId = CloudCommonIdUtil.getDefaultInstanceId(environment, hippo4JInetUtils);
        instanceId = new StringBuilder()
                .append(instanceId)
                .append(IDENTIFY_SLICER_SYMBOL)
                .append(CLIENT_IDENTIFICATION_VALUE)
                .toString();
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        instanceInfo.setInstanceId(instanceId)
                .setIpApplicationName(CloudCommonIdUtil.getIpApplicationName(environment, hippo4JInetUtils))
                .setHostName(InetAddress.getLocalHost().getHostAddress())
                .setAppName(applicationName)
                .setPort(port)
                .setClientBasePath(contextPath)
                .setGroupKey(ContentUtil.getGroupKey(itemId, namespace));
        String[] customerNetwork = DynamicThreadPoolServiceLoader.getSingletonServiceInstances(ClientNetworkService.class)
                .stream().findFirst().map(each -> each.getNetworkIpPort(environment)).orElse(null);
        String callBackUrl = new StringBuilder().append(Optional.ofNullable(customerNetwork).map(each -> each[0]).orElse(instanceInfo.getHostName())).append(":")
                .append(Optional.ofNullable(customerNetwork).map(each -> each[1]).orElse(port)).append(instanceInfo.getClientBasePath())
                .toString();
        instanceInfo.setCallBackUrl(callBackUrl);
        String identify = IdentifyUtil.generate(environment, hippo4JInetUtils);
        instanceInfo.setIdentify(identify);
        instanceInfo.setActive(active.toUpperCase());
        return instanceInfo;
    }

    @Bean
    public DiscoveryClient hippo4JDiscoveryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        return new DiscoveryClient(httpAgent, instanceInfo);
    }
}
