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

import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Apollo refresher handler.
 */
@Slf4j
public class ApolloRefresherHandler extends AbstractConfigThreadPoolDynamicRefresh {

    private static final String APOLLO_PROPERTY = "${spring.dynamic.thread-pool.apollo.namespace:application}";

    @Value(APOLLO_PROPERTY)
    private String namespace;

    @Override
    public void registerListener() {
        String[] apolloNamespaces = this.namespace.split(",");
        this.namespace = apolloNamespaces[0];
        BootstrapConfigProperties actualBootstrapConfigProperties = (BootstrapConfigProperties) bootstrapConfigProperties;
        Config config = ConfigService.getConfig(String.format("%s.%s", namespace, actualBootstrapConfigProperties.getConfigFileType().getValue()));
        ConfigChangeListener configChangeListener = configChangeEvent -> {
            String namespace = this.namespace.replaceAll("." + actualBootstrapConfigProperties.getConfigFileType().getValue(), "");
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(actualBootstrapConfigProperties.getConfigFileType().getValue());
            ConfigFile configFile = ConfigService.getConfigFile(namespace, configFileFormat);
            Map<String, Object> newChangeValueMap = new HashMap<>();
            configChangeEvent.changedKeys().stream().filter(each -> each.contains(BootstrapConfigProperties.PREFIX)).forEach(each -> {
                ConfigChange change = configChangeEvent.getChange(each);
                String newValue = change.getNewValue();
                newChangeValueMap.put(each, newValue);
            });
            dynamicRefresh(configFile.getContent(), newChangeValueMap);
        };
        config.addChangeListener(configChangeListener);
        log.info("Dynamic thread pool refresher, add apollo listener success. namespace: {}", namespace);
    }
}
