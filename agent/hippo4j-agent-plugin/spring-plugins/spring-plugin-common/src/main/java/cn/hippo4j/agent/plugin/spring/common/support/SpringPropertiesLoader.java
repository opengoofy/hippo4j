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
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import static cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties.PREFIX;

/**
 * Spring properties loader
 */
public class SpringPropertiesLoader {

    private static final ILog LOGGER = LogManager.getLogger(SpringPropertiesLoader.class);

    public static BootstrapConfigProperties BOOTSTRAP_CONFIG_PROPERTIES = new BootstrapConfigProperties();

    public static void loadSpringProperties(ConfigurableEnvironment environment) {
        Iterator<PropertySource<?>> iterator = environment.getPropertySources().iterator();
        Properties properties = new Properties();
        List<PropertySource<?>> propertySourceList = new ArrayList<>();
        while (iterator.hasNext()) {
            propertySourceList.add(iterator.next());
        }
        // Sort to ensure that the configuration in the configuration center is after the array
        // To get the latest configuration information
        propertySourceList.sort((o1, o2) -> {
            boolean o1Contains = o1.getName().toLowerCase().contains("apollo") || o1.getName().toLowerCase().contains("nacos");
            boolean o2Contains = (o2.getName().toLowerCase().contains("apollo") || o2.getName().toLowerCase().contains("nacos"));
            return Boolean.compare(o1Contains, o2Contains);
        });

        for (int i = 0; i <= propertySourceList.size() - 1; i++) {
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

        ThreadPoolCheckAlarmSupport.enableThreadPoolCheckAlarmHandler();
    }

}
