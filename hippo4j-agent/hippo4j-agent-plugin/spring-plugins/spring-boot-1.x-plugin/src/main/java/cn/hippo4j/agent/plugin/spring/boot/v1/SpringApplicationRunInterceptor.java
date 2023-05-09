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

package cn.hippo4j.agent.plugin.spring.boot.v1;

import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import cn.hippo4j.agent.core.registry.AgentThreadPoolInstanceRegistry;
import cn.hippo4j.agent.core.util.ReflectUtil;
import cn.hippo4j.agent.core.util.ThreadPoolPropertyKey;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.toolkit.BooleanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpringApplicationRunInterceptor implements InstanceMethodsAroundInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringApplicationRunInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        registerThreadPoolInstances();
        LOGGER.info("[Hippo4j-Agent] Registered thread pool instances successfully.");
        return ret;
    }

    private void registerThreadPoolInstances() {
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
    }

    private void register(String threadPoolId, ThreadPoolExecutor executor) {
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
        properties.put(ThreadPoolPropertyKey.EXECUTE_TIME_OUT, 10000L);

        // register executor.
        AgentThreadPoolInstanceRegistry.getInstance().putHolder(threadPoolId, executor, properties);

    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
