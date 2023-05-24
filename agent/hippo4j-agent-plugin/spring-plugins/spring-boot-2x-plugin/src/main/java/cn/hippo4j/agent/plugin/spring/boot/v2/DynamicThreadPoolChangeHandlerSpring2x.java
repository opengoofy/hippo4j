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

package cn.hippo4j.agent.plugin.spring.boot.v2;

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;
import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.AbstractConfigThreadPoolDynamicRefresh;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigChange;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hippo4j.agent.core.conf.Constants.SPRING_BOOT_CONFIG_PREFIX;

/**
 * Dynamic thread pool change handler spring 2x
 */
public class DynamicThreadPoolChangeHandlerSpring2x extends AbstractConfigThreadPoolDynamicRefresh {

    private static ILog LOGGER = LogManager.getLogger(DynamicThreadPoolChangeHandlerSpring2x.class);

    @Override
    public void registerListener() {
        List<String> apolloNamespaces = SpringBootConfig.Spring.Dynamic.Thread_Pool.Apollo.NAMESPACE;
        String namespace = apolloNamespaces.get(0);
        String configFileType = SpringBootConfig.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;
        Config config = ConfigService.getConfig(String.format("%s.%s", namespace, configFileType));
        ConfigChangeListener configChangeListener = configChangeEvent -> {
            String replacedNamespace = namespace.replaceAll("." + configFileType, "");
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(configFileType);
            ConfigFile configFile = ConfigService.getConfigFile(replacedNamespace, configFileFormat);
            Map<String, Object> newChangeValueMap = new HashMap<>();
            configChangeEvent.changedKeys().stream().filter(each -> each.contains(SPRING_BOOT_CONFIG_PREFIX)).forEach(each -> {
                ConfigChange change = configChangeEvent.getChange(each);
                String newValue = change.getNewValue();
                newChangeValueMap.put(each, newValue);
            });
            dynamicRefresh(configFileType, configFile.getContent(), newChangeValueMap);
        };
        config.addChangeListener(configChangeListener);
        LOGGER.info("[Hippo4j-Agent] Dynamic thread pool refresher, add apollo listener success. namespace: {}", namespace);
    }

    @Override
    public BootstrapConfigProperties buildBootstrapProperties(Map<Object, Object> configInfo) {
        BootstrapConfigProperties bindableBootstrapConfigProperties = new BootstrapConfigProperties();
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
        Binder binder = new Binder(sources);
        return binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(bindableBootstrapConfigProperties)).get();
    }
}
