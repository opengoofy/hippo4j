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
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.BootstrapConfigPropertiesBinderAdapter;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

/**
 * Bootstrap core properties binder adapt.
 */
public class DefaultBootstrapConfigPropertiesBinderAdapter implements BootstrapConfigPropertiesBinderAdapter {

    /**
     * Bootstrap core properties binder.
     *
     * @param configInfo
     * @param bootstrapConfigProperties
     * @return
     */
    @Override
    public BootstrapPropertiesInterface bootstrapCorePropertiesBinder(Map<Object, Object> configInfo, BootstrapPropertiesInterface bootstrapConfigProperties) {
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
        Binder binder = new Binder(sources);
        return binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(bootstrapConfigProperties)).get();
    }
}
