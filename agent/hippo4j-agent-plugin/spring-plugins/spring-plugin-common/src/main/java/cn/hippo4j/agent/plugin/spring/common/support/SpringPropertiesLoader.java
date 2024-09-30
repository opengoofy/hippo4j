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

package cn.hippo4j.agent.plugin.spring.common.support;

import cn.hippo4j.agent.core.boot.SpringBootConfigInitializer;
import cn.hippo4j.agent.plugin.spring.common.toolkit.SpringPropertyBinder;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.core.toolkit.inet.InetUtilsProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import lombok.Getter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties.PREFIX;

/**
 * Spring properties loader
 */
public class SpringPropertiesLoader {

    private static final ILog LOGGER = LogManager.getLogger(SpringPropertiesLoader.class);

    /**
     * A flag used to indicate whether loadSpringProperties() method has been called,
     * Used to determine whether the SpringPropertiesLoader has been initialized
     */
    @Getter
    private static final AtomicBoolean active = new AtomicBoolean(Boolean.FALSE);

    public static BootstrapConfigProperties BOOTSTRAP_CONFIG_PROPERTIES = new BootstrapConfigProperties();

    public static InetUtilsProperties INET_UTILS_PROPERTIES = new InetUtilsProperties();

    public static InetUtils inetUtils;

    public static void loadSpringProperties(ConfigurableEnvironment environment) {
        Iterator<PropertySource<?>> iterator = environment.getPropertySources().iterator();
        Properties properties = new Properties();
        List<PropertySource<?>> propertySourceList = new ArrayList<>();
        while (iterator.hasNext()) {
            propertySourceList.add(iterator.next());
        }
        // Sort to ensure that the configuration in the configuration center is after the array
        // To get the latest configuration information
        propertySourceList.sort(Comparator.comparing(
                // Make sure that Nacos boot's propertySource is placed first in the propertySourceList
                item -> !item.getClass().getName().equals("com.alibaba.nacos.spring.core.env.NacosPropertySource")));

        for (int i = propertySourceList.size() - 1; i >= 0; i--) {
            PropertySource<?> propertySource = propertySourceList.get(i);
            if (!(propertySource instanceof EnumerablePropertySource)) {
                LOGGER.warn("Skip propertySource[{}] because {} not enumerable.", propertySource.getName(), propertySource.getClass());
                continue;
            }
            LOGGER.info("Load propertySource[{}] into SpringProperties.", propertySource.getName());
            EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
            String[] keys = enumerablePropertySource.getPropertyNames();
            for (String key : keys) {
                Object value = null;
                try {
                    value = enumerablePropertySource.getProperty(key);
                    if (value != null) {
                        properties.put(key.toLowerCase(), value.toString());
                    }
                } catch (Throwable e) {
                    LOGGER.warn("Put property to spring properties failed, key=[{}], value=[{}]", key, value);
                }
            }
        }
        SpringBootConfigInitializer.setSpringProperties(properties);
        PropertiesPropertySource propertySource = new PropertiesPropertySource("customPropertySource", properties);
        environment.getPropertySources().addFirst(propertySource);
        // initialize BootstrapConfigProperties
        BOOTSTRAP_CONFIG_PROPERTIES = SpringPropertyBinder.bindProperties(environment, PREFIX, BootstrapConfigProperties.class);
        INET_UTILS_PROPERTIES = SpringPropertyBinder.bindProperties(environment, InetUtilsProperties.PREFIX, InetUtilsProperties.class);
        // Send AGENT_SPRING_PROPERTIES_LOADER_COMPLETED notification event Before active is false
        if (AbstractSubjectCenter.get(AbstractSubjectCenter.SubjectType.AGENT_SPRING_PROPERTIES_LOADER_COMPLETED) != null && Boolean.FALSE.equals(active.get())) {
            AbstractSubjectCenter.notify(AbstractSubjectCenter.SubjectType.AGENT_SPRING_PROPERTIES_LOADER_COMPLETED, () -> "");
        }
        active.set(Boolean.TRUE);
        // Enable the thread pool check alert handler
        ThreadPoolCheckAlarmSupport.enableThreadPoolCheckAlarmHandler();
        // Enable thread pool monitor handler
        ThreadPoolMonitorSupport.enableThreadPoolMonitorHandler(environment);
    }

}
