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

package cn.hippo4j.config.springboot1x.starter.refresher;

import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.BootstrapConfigPropertiesBinderAdapter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.boot.bind.CustomPropertyNamePatternsMatcher;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bootstrap core properties binder adapt.
 */
public class SpringBoot1xBootstrapConfigPropertiesBinderAdapter implements ApplicationContextAware, BootstrapConfigPropertiesBinderAdapter {

    private ApplicationContext applicationContext;

    /**
     * Bootstrap core properties binder.
     *
     * @param configInfo                config info
     * @param bootstrapConfigProperties bootstrap config properties
     * @return
     */
    @Override
    public BootstrapPropertiesInterface bootstrapCorePropertiesBinder(Map<Object, Object> configInfo, BootstrapPropertiesInterface bootstrapConfigProperties) {
        BootstrapConfigProperties bindableCoreProperties = new BootstrapConfigProperties();
        RelaxedNames relaxedNames = new RelaxedNames(BootstrapConfigProperties.PREFIX);
        Set<String> names = getNames(bindableCoreProperties, relaxedNames);
        Map<String, Object> stringConfigInfo = new HashMap<>(configInfo.size());
        configInfo.forEach((key, value) -> stringConfigInfo.put(key.toString(), value));
        MapPropertySource test = new MapPropertySource("Hippo4j", stringConfigInfo);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(test);
        PropertyValues propertyValues = CustomPropertyNamePatternsMatcher.getPropertySourcesPropertyValues(names, propertySources);
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(bindableCoreProperties, BootstrapConfigProperties.PREFIX);
        dataBinder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);
        ResourceEditorRegistrar resourceEditorRegistrar = new ResourceEditorRegistrar(applicationContext, applicationContext.getEnvironment());
        resourceEditorRegistrar.registerCustomEditors(dataBinder);
        dataBinder.bind(propertyValues);
        return bindableCoreProperties;
    }

    public static Set<String> getNames(Object target, Iterable<String> prefixes) {
        Set<String> names = new LinkedHashSet<>();
        if (target != null) {
            PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(target.getClass());
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (!"class".equals(name)) {
                    RelaxedNames relaxedNames = RelaxedNames.forCamelCase(name);
                    if (prefixes == null) {
                        for (String relaxedName : relaxedNames) {
                            names.add(relaxedName);
                        }
                    } else {
                        for (String prefix : prefixes) {
                            for (String relaxedName : relaxedNames) {
                                names.add(prefix + "." + relaxedName);
                                names.add(prefix + "_" + relaxedName);
                            }
                        }
                    }
                }
            }
        }
        return names;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
