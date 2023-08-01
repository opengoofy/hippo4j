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

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.common.api.DynamicThreadPoolAdapter;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

/**
 * <p>Adapted thread pool destroy post processor. <br />
 * The processor is used to destroy the internal {@link DynamicThreadPoolExecutor} instance
 * in the instance adapted by {@link DynamicThreadPoolAdapter} in the spring context.
 *
 * @see DynamicThreadPoolAdapter
 */
@RequiredArgsConstructor
@Slf4j
public class AdaptedThreadPoolDestroyPostProcessor implements DestructionAwareBeanPostProcessor {

    /**
     * Application context.
     */
    private final ApplicationContext applicationContext;

    /**
     * If {@link DynamicThreadPoolAdapterChoose#match} method returns true,
     * try to destroy its internal {@link DynamicThreadPoolExecutor} instance.
     *
     * @param bean the bean instance to check
     * @return {@code true} if {@link DynamicThreadPoolAdapterChoose#match} method returns true, false otherwise
     * @see DynamicThreadPoolAdapterChoose#match
     */
    @Override
    public boolean requiresDestruction(Object bean) {
        return DynamicThreadPoolAdapterChoose.match(bean);
    }

    /**
     * If the internal {@link DynamicThreadPoolExecutor} instance in the adapted bean is not managed by spring,
     * call its {@link DynamicThreadPoolExecutor#destroy()} directly.
     *
     * @param bean     the bean instance to be destroyed
     * @param beanName the name of the bean
     * @throws BeansException in case of errors
     * @see DynamicThreadPoolExecutor#destroy()
     */
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        Optional.ofNullable(DynamicThreadPoolAdapterChoose.unwrap(bean))
                .map(each -> ((DynamicThreadPoolExecutor) each).getThreadPoolId())
                // the internal thread pool is also managed by spring, no manual destruction required
                .filter(applicationContext::containsBeanDefinition)
                .map(ThreadPoolExecutorRegistry::getHolder)
                .ifPresent(executorHolder -> destroyAdaptedThreadPoolExecutor(beanName, executorHolder));
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // forked default implementation from spring-beans-5.1.14.RELEASE.jar
        // org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // forked default implementation from spring-beans-5.1.14.RELEASE.jar
        // org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
        return bean;
    }

    private void destroyAdaptedThreadPoolExecutor(String beanName, ThreadPoolExecutorHolder executorHolder) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Destroy internal dynamic thread pool '{}' for bean '{}'", executorHolder.getThreadPoolId(), beanName);
            }
            if (executorHolder.getExecutor() instanceof DynamicThreadPoolExecutor) {
                ((DynamicThreadPoolExecutor) executorHolder.getExecutor()).destroy();
            }
        } catch (Exception e) {
            log.warn("Failed to destroy internal dynamic thread pool '{}' for bean '{}'", executorHolder.getThreadPoolId(), beanName);
        }
    }
}
