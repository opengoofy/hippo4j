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

package cn.hippo4j.agent.plugin.nacos.interceptor;

import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader;
import cn.hippo4j.agent.plugin.spring.common.support.SpringThreadPoolRegisterSupport;
import cn.hippo4j.core.config.ApplicationContextHolder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Nacos Cloud config constructor interceptor
 */
public class NacosCloudAdapterConfigInstanceMethodInterceptor implements InstanceMethodsAroundInterceptor {

    private static final AtomicBoolean isExecuted = new AtomicBoolean(false);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        // This logic will only be executed once
        if (isExecuted.compareAndSet(false, true)) {
            // Get the configurable Application Context
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) allArguments[0];
            ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();

            // Remote Nacos configuration swiped into SpringPropertiesLoader
            SpringPropertiesLoader.loadSpringProperties(environment);
            // Refresh thread pool instances through configuration
            SpringThreadPoolRegisterSupport.registerThreadPoolInstances(ApplicationContextHolder.getInstance());

        }
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
