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

package cn.hippo4j.config.springboot.starter.refresher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cloud.bootstrap.config.BootstrapPropertySource;
import org.springframework.cloud.consul.config.ConsulPropertySource;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Consul refresher handler.
 */
@Slf4j
public class ConsulRefresherHandler extends AbstractConfigThreadPoolDynamicRefresh {

    private static final String CONSUL_PROPERTY = "${spring.dynamic.thread-pool.consul.data-key}";

    @Value(CONSUL_PROPERTY)
    private String dataKey;

    @EventListener(EnvironmentChangeEvent.class)
    public void refreshed(EnvironmentChangeEvent event) {


        String[] dataKeys = this.dataKey.split(",");
        this.dataKey = dataKeys[0];

        AbstractEnvironment environment = (AbstractEnvironment) ((AnnotationConfigServletWebServerApplicationContext) event.getSource()).getEnvironment();
        List<BootstrapPropertySource<?>> bootstrapPropertySourceList = environment.getPropertySources().stream()
                .filter(propertySource -> propertySource instanceof BootstrapPropertySource)
                .map(propertySource -> (BootstrapPropertySource<?>) propertySource).collect(Collectors.toList());

        Optional<BootstrapPropertySource<?>> bootstrapPropertySource = bootstrapPropertySourceList.stream()
                .filter(source -> source.getName().contains(environment.getActiveProfiles()[0])
                && source.getPropertyNames().length != 0).findFirst();

        Map<Object, Object> configInfo = new HashMap<>(64);
        if (bootstrapPropertySource.isPresent()) {
            ConsulPropertySource consulPropertySource = (ConsulPropertySource) bootstrapPropertySource.get().getDelegate();
            String[] propertyNames = consulPropertySource.getPropertyNames();
            for (String propertyName : propertyNames) {
                configInfo.put(propertyName, consulPropertySource.getProperty(propertyName));
            }

        }

        dynamicRefresh(configInfo);
    }

    @Override
    public String getProperties() {
        return null;
    }

    @Override
    public void afterPropertiesSet() {}
}
