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

package cn.hippo4j.core.toolkit;

import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Adapted to an earlier version of SpringBoot.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicThreadPoolAnnotationUtil {

    /**
     * Check for the existence of {@param annotationType} based on {@param beanName}.
     *
     * @param beanName       bean name
     * @param annotationType annotation class
     * @param <A>            the Annotation type
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        AbstractApplicationContext context = (AbstractApplicationContext) ApplicationContextHolder.getInstance();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        return Optional.of(beanFactory)
                .map(each -> (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName))
                .map(RootBeanDefinition::getResolvedFactoryMethod)
                .map(factoryMethod -> AnnotationUtils.getAnnotation(factoryMethod, annotationType))
                .orElse(null);
    }
}
