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

import cn.hippo4j.agent.core.registry.AgentThreadPoolInstanceRegistry;
import cn.hippo4j.agent.core.util.ReflectUtil;
import cn.hippo4j.agent.core.util.ThreadPoolPropertyKey;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.core.executor.support.adpter.DynamicThreadPoolAdapterChoose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Spring thread pool register support
 */
public class SpringThreadPoolRegisterSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringThreadPoolRegisterSupport.class);

    public static void registerThreadPoolInstances(ApplicationContext context) {
        Map<ThreadPoolExecutor, Class<?>> earlyConstructMap = AgentThreadPoolInstanceRegistry.getInstance().earlyConstructMap;
        for (Map.Entry<ThreadPoolExecutor, Class<?>> entry : earlyConstructMap.entrySet()) {
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
            ThreadPoolExecutor executor = null;
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
        // build parameter properties.
        Properties properties = new Properties();
        properties.put(ThreadPoolPropertyKey.THREAD_POOL_ID, threadPoolId);
        properties.put(ThreadPoolPropertyKey.CORE_POOL_SIZE, executor.getCorePoolSize());
        properties.put(ThreadPoolPropertyKey.MAXIMUM_POOL_SIZE, executor.getMaximumPoolSize());
        properties.put(ThreadPoolPropertyKey.ALLOW_CORE_THREAD_TIME_OUT, BooleanUtil.toBoolean(String.valueOf(executor.allowsCoreThreadTimeOut())));
        properties.put(ThreadPoolPropertyKey.KEEP_ALIVE_TIME, executor.getKeepAliveTime(TimeUnit.MILLISECONDS));
        properties.put(ThreadPoolPropertyKey.BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(executor.getQueue().getClass().getSimpleName()).getName());
        properties.put(ThreadPoolPropertyKey.QUEUE_CAPACITY, executor.getQueue().remainingCapacity());
        properties.put(ThreadPoolPropertyKey.THREAD_NAME_PREFIX, threadPoolId);
        properties.put(ThreadPoolPropertyKey.REJECTED_HANDLER, RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(executor.getRejectedExecutionHandler().getClass().getSimpleName()).getName());
        properties.put(ThreadPoolPropertyKey.EXECUTE_TIME_OUT, Constants.EXECUTE_TIME_OUT);
        // register executor.
        AgentThreadPoolInstanceRegistry.getInstance().putHolder(threadPoolId, executor, properties);
    }
}
