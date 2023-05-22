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

import cn.hippo4j.agent.plugin.spring.common.support.AbstractDynamicThreadPoolChangeHandlerSpring;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * Dynamic thread pool change handler spring 2x
 */
public class DynamicThreadPoolChangeHandlerSpring2x extends AbstractDynamicThreadPoolChangeHandlerSpring {

    public DynamicThreadPoolChangeHandlerSpring2x(ConfigurableApplicationContext context) {
        super(context);
    }

    protected BootstrapConfigProperties bindProperties(Map<Object, Object> configInfo, ApplicationContext applicationContext) {
        BootstrapConfigProperties bindableBootstrapConfigProperties = new BootstrapConfigProperties();
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
        Binder binder = new Binder(sources);
        return binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(bindableBootstrapConfigProperties)).get();
    }
}
