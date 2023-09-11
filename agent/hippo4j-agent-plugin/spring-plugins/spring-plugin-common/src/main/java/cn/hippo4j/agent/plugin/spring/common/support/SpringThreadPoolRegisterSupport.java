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

package cn.hippo4j.agent.plugin.spring.common.support;

import cn.hippo4j.agent.core.util.ReflectUtil;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.handler.DynamicThreadPoolAdapterChoose;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.BooleanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Spring thread pool register support
 */
public class SpringThreadPoolRegisterSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringThreadPoolRegisterSupport.class);

    public static void registerThreadPoolInstances(ApplicationContext context) {
        Map<ThreadPoolExecutor, Class<?>> referencedClassMap = ThreadPoolExecutorRegistry.REFERENCED_CLASS_MAP;
        for (Map.Entry<ThreadPoolExecutor, Class<?>> entry : referencedClassMap.entrySet()) {
            ThreadPoolExecutor enhancedInstance = entry.getKey();
            Class<?> declaredClass = entry.getValue();
            List<Field> declaredFields = ReflectUtil.getStaticFieldsFromType(declaredClass, ThreadPoolExecutor.class);
            for (Field field : declaredFields) {
                try {
                    Object value = field.get(null);
                    if (value == enhancedInstance) {
                        String threadPoolId = declaredClass.getName() + "#" + field.getName();
                        register(threadPoolId, enhancedInstance);
                        break;
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Get static field error.", e);
                }
            }
        }

        Map<String, Executor> beansWithAnnotation = context.getBeansOfType(Executor.class);
        for (Map.Entry<String, Executor> entry : beansWithAnnotation.entrySet()) {
            String beanName = entry.getKey();
            Executor bean = entry.getValue();
            ThreadPoolExecutor executor;
            if (DynamicThreadPoolAdapterChoose.match(bean)) {
                executor = DynamicThreadPoolAdapterChoose.unwrap(bean);
            } else {
                executor = (ThreadPoolExecutor) bean;
            }
            if (executor == null) {
                LOGGER.warn("[Hippo4j-Agent] Thread pool is null, ignore bean registration. beanName={}, beanClass={}", beanName, bean.getClass().getName());
            } else {
                register(beanName, executor);
            }
        }
        LOGGER.info("[Hippo4j-Agent] Registered thread pool instances successfully.");
    }

    public static void register(String threadPoolId, ThreadPoolExecutor executor) {
        if (executor == null) {
            return;
        }
        ExecutorProperties executorProperties = ExecutorProperties.builder()
                .threadPoolId(threadPoolId)
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .allowCoreThreadTimeOut(BooleanUtil.toBoolean(String.valueOf(executor.allowsCoreThreadTimeOut())))
                .blockingQueue(BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(executor.getQueue().getClass().getSimpleName()).getName())
                .queueCapacity(executor.getQueue().remainingCapacity())
                .rejectedHandler(RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(executor.getRejectedExecutionHandler().getClass().getSimpleName()).getName())
                .build();
        ThreadPoolExecutorRegistry.putHolder(threadPoolId, executor, executorProperties);
    }
}
