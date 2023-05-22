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

import cn.hippo4j.agent.core.registry.AgentThreadPoolExecutorHolder;
import cn.hippo4j.agent.core.registry.AgentThreadPoolInstanceRegistry;
import cn.hippo4j.agent.core.util.ThreadPoolPropertyKey;
import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.executor.support.ResizableCapacityLinkedBlockingQueue;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigFileTypeEnum;
import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigParserHandler;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.agent.core.conf.Constants.SPRING_BOOT_CONFIG_PREFIX;

/**
 * Abstract dynamic thread poo change handler spring
 */
public abstract class AbstractDynamicThreadPoolChangeHandlerSpring implements ThreadPoolDynamicRefresh {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDynamicThreadPoolChangeHandlerSpring.class);

    private final ConfigurableApplicationContext applicationContext;

    public AbstractDynamicThreadPoolChangeHandlerSpring(ConfigurableApplicationContext context) {
        this.applicationContext = context;
    }

    public void registerListener() {

        List<String> apolloNamespaces = SpringBootConfig.Spring.Dynamic.Thread_Pool.Apollo.NAMESPACE;
        String namespace = apolloNamespaces.get(0);
        String configFileType = SpringBootConfig.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;

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
            dynamicRefresh(configFile.getContent(), newChangeValueMap, applicationContext);
        };
        config.addChangeListener(configChangeListener);
        LOGGER.info("[Hippo4j-Agent] Dynamic thread pool refresher, add apollo listener success. namespace: {}", namespace);
    }

    private void dynamicRefresh(String configContent, Map<String, Object> newValueChangeMap, ApplicationContext context) {
        try {
            String configFileType = SpringBootConfig.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;

            Map<Object, Object> afterConfigMap = ConfigParserHandler.getInstance().parseConfig(configContent,
                    ConfigFileTypeEnum.of(configFileType));
            if (CollectionUtil.isNotEmpty(newValueChangeMap)) {
                Optional.ofNullable(afterConfigMap).ifPresent(each -> each.putAll(newValueChangeMap));
            }
            // TODO
            /*
             * BootstrapConfigProperties afterConfigProperties = bindProperties(afterConfigMap, context);
             * 
             * List<ExecutorProperties> executors = afterConfigProperties.getExecutors(); for (ExecutorProperties afterProperties : executors) { String threadPoolId =
             * afterProperties.getThreadPoolId(); AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(threadPoolId); if (holder.isEmpty() ||
             * holder.getExecutor() == null) { continue; } ExecutorProperties beforeProperties = convert(holder.getProperties());
             * 
             * if (!checkConsistency(threadPoolId, beforeProperties, afterProperties)) { continue; }
             * 
             * dynamicRefreshPool(beforeProperties, afterProperties); holder.setProperties(failDefaultExecutorProperties(beforeProperties, afterProperties)); // do refresh.
             * ChangeParameterNotifyRequest changeRequest = buildChangeRequest(beforeProperties, afterProperties); LOGGER.info(CHANGE_THREAD_POOL_TEXT, threadPoolId, String.format(CHANGE_DELIMITER,
             * beforeProperties.getCorePoolSize(), changeRequest.getNowCorePoolSize()), String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), changeRequest.getNowMaximumPoolSize()),
             * String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), changeRequest.getNowQueueCapacity()), String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(),
             * changeRequest.getNowKeepAliveTime()), String.format(CHANGE_DELIMITER, beforeProperties.getExecuteTimeOut(), changeRequest.getNowExecuteTimeOut()), String.format(CHANGE_DELIMITER,
             * beforeProperties.getRejectedHandler(), changeRequest.getNowRejectedName()), String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(),
             * changeRequest.getNowAllowsCoreThreadTimeOut())); }
             */
        } catch (Exception ex) {
            LOGGER.error("[Hippo4j-Agent] config mode dynamic refresh failed.", ex);
        }
    }

    /**
     * Dynamic refresh pool.
     */
    private void dynamicRefreshPool(ExecutorProperties beforeProperties, ExecutorProperties afterProperties) {
        AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(afterProperties.getThreadPoolId());
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
     * @param afterProperties  new properties
     * @return executor properties
     */
    private Properties failDefaultExecutorProperties(ExecutorProperties beforeProperties, ExecutorProperties afterProperties) {
        return convert(ExecutorProperties.builder()
                .corePoolSize(Optional.ofNullable(afterProperties.getCorePoolSize()).orElse(beforeProperties.getCorePoolSize()))
                .maximumPoolSize(Optional.ofNullable(afterProperties.getMaximumPoolSize()).orElse(beforeProperties.getMaximumPoolSize()))
                .blockingQueue(afterProperties.getBlockingQueue())
                .queueCapacity(Optional.ofNullable(afterProperties.getQueueCapacity()).orElse(beforeProperties.getQueueCapacity()))
                .keepAliveTime(Optional.ofNullable(afterProperties.getKeepAliveTime()).orElse(beforeProperties.getKeepAliveTime()))
                .executeTimeOut(Optional.ofNullable(afterProperties.getExecuteTimeOut()).orElse(beforeProperties.getExecuteTimeOut()))
                .rejectedHandler(Optional.ofNullable(afterProperties.getRejectedHandler()).orElse(beforeProperties.getRejectedHandler()))
                .allowCoreThreadTimeOut(Optional.ofNullable(afterProperties.getAllowCoreThreadTimeOut()).orElse(beforeProperties.getAllowCoreThreadTimeOut()))
                .threadPoolId(beforeProperties.getThreadPoolId())
                .build());
    }

    private ExecutorProperties convert(Properties properties) {
        return ExecutorProperties.builder()
                .threadPoolId((String) properties.get(ThreadPoolPropertyKey.THREAD_POOL_ID))
                .corePoolSize((Integer) properties.get(ThreadPoolPropertyKey.CORE_POOL_SIZE))
                .maximumPoolSize((Integer) properties.get(ThreadPoolPropertyKey.MAXIMUM_POOL_SIZE))
                .allowCoreThreadTimeOut((Boolean) properties.get(ThreadPoolPropertyKey.ALLOW_CORE_THREAD_TIME_OUT))
                .keepAliveTime((Long) properties.get(ThreadPoolPropertyKey.KEEP_ALIVE_TIME))
                .blockingQueue((String) properties.get(ThreadPoolPropertyKey.BLOCKING_QUEUE))
                .queueCapacity((Integer) properties.get(ThreadPoolPropertyKey.QUEUE_CAPACITY))
                .threadNamePrefix((String) properties.get(ThreadPoolPropertyKey.THREAD_NAME_PREFIX))
                .rejectedHandler((String) properties.get(ThreadPoolPropertyKey.REJECTED_HANDLER))
                .executeTimeOut((Long) properties.get(ThreadPoolPropertyKey.EXECUTE_TIME_OUT))
                .build();
    }

    private Properties convert(ExecutorProperties executorProperties) {
        Properties properties = new Properties();
        Optional.ofNullable(executorProperties.getCorePoolSize()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.CORE_POOL_SIZE, v));
        Optional.ofNullable(executorProperties.getMaximumPoolSize()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.MAXIMUM_POOL_SIZE, v));
        Optional.ofNullable(executorProperties.getBlockingQueue()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.BLOCKING_QUEUE, v));
        Optional.ofNullable(executorProperties.getQueueCapacity()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.QUEUE_CAPACITY, v));
        Optional.ofNullable(executorProperties.getKeepAliveTime()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.KEEP_ALIVE_TIME, v));
        Optional.ofNullable(executorProperties.getExecuteTimeOut()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.EXECUTE_TIME_OUT, v));
        Optional.ofNullable(executorProperties.getRejectedHandler()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.REJECTED_HANDLER, v));
        Optional.ofNullable(executorProperties.getAllowCoreThreadTimeOut()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.ALLOW_CORE_THREAD_TIME_OUT, v));
        Optional.ofNullable(executorProperties.getThreadPoolId()).ifPresent(v -> properties.put(ThreadPoolPropertyKey.THREAD_POOL_ID, v));
        return properties;
    }

    /**
     * Construct change parameter notify request instance.
     *
     * @param beforeProperties old properties
     * @param afterProperties  new properties
     * @return instance
     */
    /*
     * private ChangeParameterNotifyRequest buildChangeRequest(ExecutorProperties beforeProperties, ExecutorProperties afterProperties) { ChangeParameterNotifyRequest changeParameterNotifyRequest =
     * ChangeParameterNotifyRequest.builder() .beforeCorePoolSize(beforeProperties.getCorePoolSize()) .beforeMaximumPoolSize(beforeProperties.getMaximumPoolSize())
     * .beforeAllowsCoreThreadTimeOut(beforeProperties.getAllowCoreThreadTimeOut()) .beforeKeepAliveTime(beforeProperties.getKeepAliveTime()) .beforeQueueCapacity(beforeProperties.getQueueCapacity())
     * .beforeRejectedName(beforeProperties.getRejectedHandler()) .beforeExecuteTimeOut(beforeProperties.getExecuteTimeOut()) .blockingQueueName(afterProperties.getBlockingQueue())
     * .nowCorePoolSize(Optional.ofNullable(afterProperties.getCorePoolSize()).orElse(beforeProperties.getCorePoolSize()))
     * .nowMaximumPoolSize(Optional.ofNullable(afterProperties.getMaximumPoolSize()).orElse(beforeProperties.getMaximumPoolSize()))
     * .nowAllowsCoreThreadTimeOut(Optional.ofNullable(afterProperties.getAllowCoreThreadTimeOut()).orElse(beforeProperties.getAllowCoreThreadTimeOut()))
     * .nowKeepAliveTime(Optional.ofNullable(afterProperties.getKeepAliveTime()).orElse(beforeProperties.getKeepAliveTime()))
     * .nowQueueCapacity(Optional.ofNullable(afterProperties.getQueueCapacity()).orElse(beforeProperties.getQueueCapacity()))
     * .nowRejectedName(Optional.ofNullable(afterProperties.getRejectedHandler()).orElse(beforeProperties.getRejectedHandler()))
     * .nowExecuteTimeOut(Optional.ofNullable(afterProperties.getExecuteTimeOut()).orElse(beforeProperties.getExecuteTimeOut())) .build();
     * changeParameterNotifyRequest.setThreadPoolId(beforeProperties.getThreadPoolId()); return changeParameterNotifyRequest; }
     */

    /**
     * Check consistency.
     *
     * @param threadPoolId
     * @param afterProperties
     */
    private boolean checkConsistency(String threadPoolId, ExecutorProperties beforeProperties, ExecutorProperties afterProperties) {
        AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(threadPoolId);
        if (holder.isEmpty() || holder.getExecutor() == null) {
            return false;
        }
        ThreadPoolExecutor executor = holder.getExecutor();
        return (afterProperties.getCorePoolSize() != null && !Objects.equals(beforeProperties.getCorePoolSize(), afterProperties.getCorePoolSize()))
                || (afterProperties.getMaximumPoolSize() != null && !Objects.equals(beforeProperties.getMaximumPoolSize(), afterProperties.getMaximumPoolSize()))
                || (afterProperties.getAllowCoreThreadTimeOut() != null && !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), afterProperties.getAllowCoreThreadTimeOut()))
                || (afterProperties.getExecuteTimeOut() != null && !Objects.equals(beforeProperties.getExecuteTimeOut(), afterProperties.getExecuteTimeOut()))
                || (afterProperties.getKeepAliveTime() != null && !Objects.equals(beforeProperties.getKeepAliveTime(), afterProperties.getKeepAliveTime()))
                || (afterProperties.getRejectedHandler() != null && !Objects.equals(beforeProperties.getRejectedHandler(), afterProperties.getRejectedHandler()))
                ||
                ((afterProperties.getQueueCapacity() != null && !Objects.equals(beforeProperties.getQueueCapacity(), afterProperties.getQueueCapacity())
                        && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName(), executor.getQueue().getClass().getSimpleName())));
    }

}
