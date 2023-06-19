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
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
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

    private static final int INITIAL_CAPACITY = 64;

    @EventListener(EnvironmentChangeEvent.class)
    public void refreshed(EnvironmentChangeEvent event) {
        Map<String, Object> configInfo = extractLatestConfigInfo(event);
        dynamicRefresh(StringUtils.EMPTY, configInfo);
    }

    private Map<String, Object> extractLatestConfigInfo(EnvironmentChangeEvent event) {
        AbstractEnvironment environment = (AbstractEnvironment) ((AnnotationConfigServletWebServerApplicationContext) event.getSource()).getEnvironment();
        String activeProfile = Optional.ofNullable(environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : null)
                .orElseGet(() -> String.valueOf(getApplicationConfigDefaultContext(environment)));
        List<BootstrapPropertySource<?>> bootstrapPropertySourceList = environment.getPropertySources().stream()
                .filter(propertySource -> propertySource instanceof BootstrapPropertySource)
                .map(propertySource -> (BootstrapPropertySource<?>) propertySource).collect(Collectors.toList());
        Optional<BootstrapPropertySource<?>> bootstrapPropertySource = bootstrapPropertySourceList.stream()
                .filter(source -> source.getName().contains(activeProfile) && source.getPropertyNames().length != 0).findFirst();
        Map<String, Object> configInfo = new HashMap<>(INITIAL_CAPACITY);
        if (bootstrapPropertySource.isPresent()) {
            ConsulPropertySource consulPropertySource = (ConsulPropertySource) bootstrapPropertySource.get().getDelegate();
            String[] propertyNames = consulPropertySource.getPropertyNames();
            for (String propertyName : propertyNames) {
                configInfo.put(propertyName, consulPropertySource.getProperty(propertyName));
            }
        }
        return configInfo;
    }

    private CharSequence getApplicationConfigDefaultContext(AbstractEnvironment environment) {
        return environment.getPropertySources().stream()
                .filter(propertySource -> propertySource instanceof OriginTrackedMapPropertySource)
                .map(propertySource -> ((Map<String, CharSequence>) propertySource.getSource()).get("spring.cloud.consul.config.default-context"))
                .findFirst().orElse(StringUtils.EMPTY);
    }

    @Override
    public void registerListener() {
        // The listener has been registered by annotation.
    }
}
