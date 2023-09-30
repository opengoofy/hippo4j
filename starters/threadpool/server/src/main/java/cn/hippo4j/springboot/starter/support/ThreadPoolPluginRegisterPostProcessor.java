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

package cn.hippo4j.springboot.starter.support;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.core.executor.plugin.ThreadPoolPlugin;
import cn.hippo4j.core.executor.plugin.manager.DefaultGlobalThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.GlobalThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginRegistrar;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;

import java.util.Objects;

/**
 * <p>The extension implementation of {@link GlobalThreadPoolPluginManager} and {@link BeanPostProcessor},
 * used to register {@link ThreadPoolPlugin} for the bean initialization stage of the {@link ThreadPoolPluginSupport}.
 *
 * <p><b>NOTE:</b>
 * If the {@link ThreadPoolPlugin}, {@link ThreadPoolPluginRegistrar}, and {@link ThreadPoolPluginSupport} is set to lazy load,
 * The processor will not perceive the bean unless the user actively triggers the initialization of the bean.
 *
 * @see ThreadPoolPluginSupport
 * @see ThreadPoolPluginRegistrar
 * @see ThreadPoolPlugin
 * @see GlobalThreadPoolPluginManager
 * @see DefaultGlobalThreadPoolPluginManager
 */
@Slf4j
public class ThreadPoolPluginRegisterPostProcessor extends DefaultGlobalThreadPoolPluginManager implements BeanPostProcessor, ApplicationContextAware {

    /**
     * Application context
     */
    private ConfigurableListableBeanFactory beanFactory;

    /**
     * <p>Post process bean, if bean is instance of {@link ThreadPoolPlugin},
     * {@link ThreadPoolPluginRegistrar} or {@link ThreadPoolPluginSupport},
     * then take beans as an available component and register to {@link GlobalThreadPoolPluginManager}.
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one;
     * if {@code null}, no subsequent BeanPostProcessors will be invoked
     * @throws BeansException in case of errors
     * @see GlobalThreadPoolPluginManager#enableThreadPoolPlugin
     * @see GlobalThreadPoolPluginManager#enableThreadPoolPluginRegistrar
     * @see GlobalThreadPoolPluginManager#registerThreadPoolPluginSupport
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanType = null;
        try {
            beanType = AutoProxyUtils.determineTargetClass(beanFactory, beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
            }
        }
        if (Objects.isNull(beanType)) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot resolve type for bean [{}]", beanName);
            }
            return bean;
        }

        // register bean if necessary
        registerThreadPoolPluginRegistrarIfNecessary(bean, beanType);
        registerThreadPoolPluginIfNecessary(bean, beanType);
        registerThreadPoolPluginSupportIfNecessary(bean, beanType);
        return bean;
    }

    private void registerThreadPoolPluginSupportIfNecessary(Object bean, Class<?> beanType) {
        if (ThreadPoolPluginSupport.class.isAssignableFrom(beanType)) {
            ThreadPoolPluginSupport support = (ThreadPoolPluginSupport) bean;
            if (registerThreadPoolPluginSupport(support) && log.isDebugEnabled()) {
                log.debug("Register ThreadPoolPluginSupport [{}]", support.getThreadPoolId());
            }
        }
    }

    private void registerThreadPoolPluginIfNecessary(Object bean, Class<?> beanType) {
        if (ThreadPoolPlugin.class.isAssignableFrom(beanType)) {
            ThreadPoolPlugin plugin = (ThreadPoolPlugin) bean;
            if (enableThreadPoolPlugin(plugin) && log.isDebugEnabled()) {
                log.debug("Register ThreadPoolPlugin [{}]", plugin.getId());
            }
        }
    }

    private void registerThreadPoolPluginRegistrarIfNecessary(Object bean, Class<?> beanType) {
        if (ThreadPoolPluginRegistrar.class.isAssignableFrom(beanType)) {
            ThreadPoolPluginRegistrar registrar = (ThreadPoolPluginRegistrar) bean;
            if (enableThreadPoolPluginRegistrar(registrar) && log.isDebugEnabled()) {
                log.debug("Register ThreadPoolPluginRegistrar [{}]", registrar.getId());
            }
        }
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        Assert.isTrue(
                factory instanceof ConfigurableListableBeanFactory,
                "Factory cannot cast to ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) factory;
    }

    /**
     * Apply this {@code BeanPostProcessor} to the given new bean instance <i>before</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * <p>The default implementation returns the given {@code bean} as-is.
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one;
     * if {@code null}, no subsequent BeanPostProcessors will be invoked
     * @throws BeansException in case of errors
     * @see InitializingBean#afterPropertiesSet
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
