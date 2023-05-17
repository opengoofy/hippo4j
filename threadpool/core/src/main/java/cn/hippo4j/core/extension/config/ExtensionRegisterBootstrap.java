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

package cn.hippo4j.core.extension.config;

import cn.hippo4j.core.extension.IExtension;
import cn.hippo4j.core.extension.annotation.Realization;
import cn.hippo4j.core.extension.support.ExtensionRegistry;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Extension register bootstrap
 */
public class ExtensionRegisterBootstrap implements ApplicationContextAware, ApplicationRunner {

    private ExtensionRegistry registry = ExtensionRegistry.getInstance();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        applicationContext.getBeansWithAnnotation(Realization.class)
                .entrySet().stream()
                .filter(entry -> !filterClass(entry.getKey(), entry.getValue()))
                .forEach(entry -> registry.register((IExtension) entry.getValue()));
    }

    private boolean filterClass(String beanName, Object bean) {
        return bean.getClass().isAssignableFrom(IExtension.class)
                || ScopedProxyUtils.isScopedTarget(beanName)
                || !(bean instanceof IExtension);
    }
}
