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

package cn.hippo4j.agent.plugin.spring.boot.v1.interceptor;

import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import cn.hippo4j.agent.plugin.spring.common.event.DynamicThreadPoolRefreshListener;
import cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader;
import cn.hippo4j.agent.plugin.spring.common.support.SpringThreadPoolRegisterSupport;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.core.config.ApplicationContextHolder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * EventPublishingRunListener Finished interceptor
 */
public class EventPublishingRunListenerFinishedInterceptorV1 implements InstanceMethodsAroundInterceptor {

    private static final AtomicBoolean isExecuted = new AtomicBoolean(false);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        // Since the finished() method is a method of the EventPublishingRunListener class,
        // the EventPublishingRunListener the first parameter is ConfigurableApplicationContext
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) allArguments[0];
        if (context.getParent() != null) {
            // After the child container is started, the thread pool registration will be carried out
            // IDEA's runtime environment or debugging mechanisms make context refresh speeds different.
            // Ensure that thread pool registration logic is executed only after the context is fully started
            if (context.isActive()) {
                SpringThreadPoolRegisterSupport.registerThreadPoolInstances(context);
                return ret;
            }
            // However, the packaged JAR runtime may refresh the context faster
            // resulting in the context not being refreshed yet when registerThreadPoolInstances is called
            // Register listener to handle the registration after the context has been fully refreshed
            context.addApplicationListener((ApplicationListener<ContextRefreshedEvent>) event -> {
                SpringThreadPoolRegisterSupport.registerThreadPoolInstances(event.getApplicationContext());
            });
            return ret;
        }
        // This logic will only be executed once
        if (isExecuted.compareAndSet(false, true)) {
            ApplicationContextHolder contextHolder = new ApplicationContextHolder();
            contextHolder.setApplicationContext(context);
            // Load Spring Properties
            SpringPropertiesLoader.loadSpringProperties(context.getEnvironment());
            // register Dynamic ThreadPool Refresh Listener
            if (AbstractSubjectCenter.get(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH) == null) {
                AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH, new DynamicThreadPoolRefreshListener());
            }
        }
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
