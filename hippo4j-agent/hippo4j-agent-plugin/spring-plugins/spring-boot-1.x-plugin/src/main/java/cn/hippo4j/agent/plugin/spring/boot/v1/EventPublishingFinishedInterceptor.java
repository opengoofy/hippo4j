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

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import cn.hippo4j.agent.core.registry.AgentThreadPoolExecutorHolder;
import cn.hippo4j.agent.core.registry.AgentThreadPoolInstanceRegistry;
import cn.hippo4j.agent.plugin.spring.common.SpringPropertiesLoader;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.MapUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.common.config.ExecutorProperties;
import cn.hippo4j.config.springboot.starter.parser.ConfigFileTypeEnum;
import cn.hippo4j.config.springboot.starter.parser.ConfigParserHandler;
import cn.hippo4j.config.springboot.starter.support.GlobalCoreThreadPoolManage;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.boot.bind.CustomPropertyNamePatternsMatcher;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.agent.core.conf.Constants.SPRING_BOOT_CONFIG_PREFIX;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;
import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_THREAD_POOL_TEXT;
import static cn.hippo4j.config.springboot1x.starter.refresher.SpringBoot1xBootstrapConfigPropertiesBinderAdapt.getNames;

public class EventPublishingFinishedInterceptor implements InstanceMethodsAroundInterceptor {

    private static final ILog FILE_LOGGER = LogManager.getLogger(EventPublishingFinishedInterceptor.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublishingFinishedInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) allArguments[0];
        SpringPropertiesLoader.loadSpringProperties(context.getEnvironment());

        List<String> apolloNamespaces = ApolloSpringBootProperties.Spring.Dynamic.Thread_Pool.Apollo.NAMESPACE;

        String namespace = apolloNamespaces.get(0);
        String configFileType = ApolloSpringBootProperties.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;
        com.ctrip.framework.apollo.Config config = ConfigService.getConfig(String.format("%s.%s", namespace, configFileType));
        ConfigChangeListener configChangeListener = configChangeEvent -> {
            String replacedNamespace = namespace.replaceAll("." + configFileType, "");
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(configFileType);
            ConfigFile configFile = ConfigService.getConfigFile(replacedNamespace, configFileFormat);
            Map<String, Object> newChangeValueMap = new HashMap<>();
            configChangeEvent.changedKeys().stream().filter(each -> each.contains(SPRING_BOOT_CONFIG_PREFIX)).forEach(each -> {
                ConfigChange change = configChangeEvent.getChange(each);
                String newValue = change.getNewValue();
                newChangeValueMap.put(each, newValue);
            });
            dynamicRefresh(configFile.getContent(), newChangeValueMap, context);
        };
        config.addChangeListener(configChangeListener);
        LOGGER.info("Dynamic thread pool refresher, add apollo listener success. namespace: {}", namespace);
        return ret;
    }

    public BootstrapConfigProperties bindProperties(Map<Object, Object> configInfo, ApplicationContext applicationContext) {
        BootstrapConfigProperties bindableCoreProperties = new BootstrapConfigProperties();
        if (MapUtil.isEmpty(configInfo)) {
            return bindableCoreProperties;
        }
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

    public void dynamicRefresh(String configContent, Map<String, Object> newValueChangeMap, ApplicationContext context) {
        try {
            String configFileType = ApolloSpringBootProperties.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;

            Map<Object, Object> afterConfigMap = ConfigParserHandler.getInstance().parseConfig(configContent,
                    ConfigFileTypeEnum.of(configFileType));
            if (CollectionUtil.isNotEmpty(newValueChangeMap)) {
                Optional.ofNullable(afterConfigMap).ifPresent(each -> each.putAll(newValueChangeMap));
            }
            BootstrapConfigProperties afterConfigProperties = bindProperties(afterConfigMap, context);

            List<ExecutorProperties> executors = afterConfigProperties.getExecutors();
            for (ExecutorProperties properties : executors) {
                String threadPoolId = properties.getThreadPoolId();
                // if (!match(properties)) {
                // continue;
                // }
                if (!checkConsistency(threadPoolId, properties)) {
                    continue;
                }

                dynamicRefreshPool(threadPoolId, properties);
                AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(properties.getThreadPoolId());
                ExecutorProperties beforeProperties = holder.getProperties();
                holder.setProperties(failDefaultExecutorProperties(beforeProperties, properties)); // do refresh.
                ChangeParameterNotifyRequest changeRequest = buildChangeRequest(beforeProperties, properties);
                LOGGER.info(CHANGE_THREAD_POOL_TEXT,
                        threadPoolId,
                        String.format(CHANGE_DELIMITER, beforeProperties.getCorePoolSize(), changeRequest.getNowCorePoolSize()),
                        String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), changeRequest.getNowMaximumPoolSize()),
                        String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), changeRequest.getNowQueueCapacity()),
                        String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(), changeRequest.getNowKeepAliveTime()),
                        String.format(CHANGE_DELIMITER, beforeProperties.getExecuteTimeOut(), changeRequest.getNowExecuteTimeOut()),
                        String.format(CHANGE_DELIMITER, beforeProperties.getRejectedHandler(), changeRequest.getNowRejectedName()),
                        String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(), changeRequest.getNowAllowsCoreThreadTimeOut()));
            }
        } catch (Exception ex) {
            LOGGER.error("[Hippo4j-Agent] config mode dynamic refresh failed.", ex);
        }
    }

    /**
     * Dynamic refresh pool.
     */
    private void dynamicRefreshPool(String threadPoolId, ExecutorProperties afterProperties) {
        AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(afterProperties.getThreadPoolId());
        ExecutorProperties beforeProperties = holder.getProperties();
        ThreadPoolExecutor executor = holder.getExecutor();
        if (afterProperties.getMaximumPoolSize() != null && afterProperties.getCorePoolSize() != null) {
            ThreadPoolExecutorUtil.safeSetPoolSize(executor, afterProperties.getCorePoolSize(), afterProperties.getMaximumPoolSize());
        } else {
            if (afterProperties.getMaximumPoolSize() != null) {
                executor.setMaximumPoolSize(afterProperties.getMaximumPoolSize());
            }
            if (afterProperties.getCorePoolSize() != null) {
                executor.setCorePoolSize(afterProperties.getCorePoolSize());
            }
        }
        if (afterProperties.getAllowCoreThreadTimeOut() != null && !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), afterProperties.getAllowCoreThreadTimeOut())) {
            executor.allowCoreThreadTimeOut(afterProperties.getAllowCoreThreadTimeOut());
        }
        if (afterProperties.getExecuteTimeOut() != null && !Objects.equals(beforeProperties.getExecuteTimeOut(), afterProperties.getExecuteTimeOut())) {
            if (executor instanceof DynamicThreadPoolExecutor) {
                ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(afterProperties.getExecuteTimeOut());
            }
        }
        if (afterProperties.getRejectedHandler() != null && !Objects.equals(beforeProperties.getRejectedHandler(), afterProperties.getRejectedHandler())) {
            RejectedExecutionHandler rejectedExecutionHandler = RejectedPolicyTypeEnum.createPolicy(afterProperties.getRejectedHandler());
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }
        if (afterProperties.getKeepAliveTime() != null && !Objects.equals(beforeProperties.getKeepAliveTime(), afterProperties.getKeepAliveTime())) {
            executor.setKeepAliveTime(afterProperties.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        if (afterProperties.getQueueCapacity() != null && !Objects.equals(beforeProperties.getQueueCapacity(), afterProperties.getQueueCapacity())
                && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName(), executor.getQueue().getClass().getSimpleName())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                ResizableCapacityLinkedBlockingQueue<?> queue = (ResizableCapacityLinkedBlockingQueue<?>) executor.getQueue();
                queue.setCapacity(afterProperties.getQueueCapacity());
            } else {
                LOGGER.warn("The queue length cannot be modified. Queue type mismatch. Current queue type: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
    }

    /**
     * Fail default executor properties.
     *
     * @param beforeProperties old properties
     * @param properties       new properties
     * @return executor properties
     */
    private ExecutorProperties failDefaultExecutorProperties(ExecutorProperties beforeProperties, ExecutorProperties properties) {
        return ExecutorProperties.builder()
                .corePoolSize(Optional.ofNullable(properties.getCorePoolSize()).orElse(beforeProperties.getCorePoolSize()))
                .maximumPoolSize(Optional.ofNullable(properties.getMaximumPoolSize()).orElse(beforeProperties.getMaximumPoolSize()))
                .blockingQueue(properties.getBlockingQueue())
                .queueCapacity(Optional.ofNullable(properties.getQueueCapacity()).orElse(beforeProperties.getQueueCapacity()))
                .keepAliveTime(Optional.ofNullable(properties.getKeepAliveTime()).orElse(beforeProperties.getKeepAliveTime()))
                .executeTimeOut(Optional.ofNullable(properties.getExecuteTimeOut()).orElse(beforeProperties.getExecuteTimeOut()))
                .rejectedHandler(Optional.ofNullable(properties.getRejectedHandler()).orElse(beforeProperties.getRejectedHandler()))
                .allowCoreThreadTimeOut(Optional.ofNullable(properties.getAllowCoreThreadTimeOut()).orElse(beforeProperties.getAllowCoreThreadTimeOut()))
                .threadPoolId(beforeProperties.getThreadPoolId())
                .build();
    }

    /**
     * Construct change parameter notify request instance.
     *
     * @param beforeProperties old properties
     * @param properties       new properties
     * @return instance
     */
    private ChangeParameterNotifyRequest buildChangeRequest(ExecutorProperties beforeProperties, ExecutorProperties properties) {
        ChangeParameterNotifyRequest changeParameterNotifyRequest = ChangeParameterNotifyRequest.builder()
                .beforeCorePoolSize(beforeProperties.getCorePoolSize())
                .beforeMaximumPoolSize(beforeProperties.getMaximumPoolSize())
                .beforeAllowsCoreThreadTimeOut(beforeProperties.getAllowCoreThreadTimeOut())
                .beforeKeepAliveTime(beforeProperties.getKeepAliveTime())
                .beforeQueueCapacity(beforeProperties.getQueueCapacity())
                .beforeRejectedName(beforeProperties.getRejectedHandler())
                .beforeExecuteTimeOut(beforeProperties.getExecuteTimeOut())
                .blockingQueueName(properties.getBlockingQueue())
                .nowCorePoolSize(Optional.ofNullable(properties.getCorePoolSize()).orElse(beforeProperties.getCorePoolSize()))
                .nowMaximumPoolSize(Optional.ofNullable(properties.getMaximumPoolSize()).orElse(beforeProperties.getMaximumPoolSize()))
                .nowAllowsCoreThreadTimeOut(Optional.ofNullable(properties.getAllowCoreThreadTimeOut()).orElse(beforeProperties.getAllowCoreThreadTimeOut()))
                .nowKeepAliveTime(Optional.ofNullable(properties.getKeepAliveTime()).orElse(beforeProperties.getKeepAliveTime()))
                .nowQueueCapacity(Optional.ofNullable(properties.getQueueCapacity()).orElse(beforeProperties.getQueueCapacity()))
                .nowRejectedName(Optional.ofNullable(properties.getRejectedHandler()).orElse(beforeProperties.getRejectedHandler()))
                .nowExecuteTimeOut(Optional.ofNullable(properties.getExecuteTimeOut()).orElse(beforeProperties.getExecuteTimeOut()))
                .build();
        changeParameterNotifyRequest.setThreadPoolId(beforeProperties.getThreadPoolId());
        return changeParameterNotifyRequest;
    }

    /**
     * Check consistency.
     *
     * @param threadPoolId
     * @param properties
     */
    private boolean checkConsistency(String threadPoolId, ExecutorProperties properties) {
        AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(threadPoolId);
        if (holder.isEmpty() || holder.getExecutor() == null) {
            return false;
        }
        ThreadPoolExecutor executor = holder.getExecutor();
        ExecutorProperties beforeProperties = holder.getProperties();
        return (properties.getCorePoolSize() != null && !Objects.equals(beforeProperties.getCorePoolSize(), properties.getCorePoolSize()))
                || (properties.getMaximumPoolSize() != null && !Objects.equals(beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()))
                || (properties.getAllowCoreThreadTimeOut() != null && !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut()))
                || (properties.getExecuteTimeOut() != null && !Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut()))
                || (properties.getKeepAliveTime() != null && !Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()))
                || (properties.getRejectedHandler() != null && !Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler()))
                ||
                ((properties.getQueueCapacity() != null && !Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                        && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName(), executor.getQueue().getClass().getSimpleName())));
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
