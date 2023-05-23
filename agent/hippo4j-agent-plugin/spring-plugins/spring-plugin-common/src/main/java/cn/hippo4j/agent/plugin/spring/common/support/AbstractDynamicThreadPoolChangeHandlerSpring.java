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

import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hippo4j.agent.core.conf.Constants.SPRING_BOOT_CONFIG_PREFIX;

/**
 * Abstract dynamic thread poo change handler spring
 */
public abstract class AbstractDynamicThreadPoolChangeHandlerSpring implements ThreadPoolDynamicRefresh {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDynamicThreadPoolChangeHandlerSpring.class);

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
            dynamicRefresh(configFile.getContent(), newChangeValueMap);
        };
        config.addChangeListener(configChangeListener);
        LOGGER.info("[Hippo4j-Agent] Dynamic thread pool refresher, add apollo listener success. namespace: {}", namespace);
    }

    public void dynamicRefresh(String configContent, Map<String, Object> newValueChangeMap) {
        try {
            // String configFileType = SpringBootConfig.Spring.Dynamic.Thread_Pool.CONFIG_FILE_TYPE;
            //
            // Map<Object, Object> afterConfigMap = ConfigParserHandler.getInstance().parseConfig(configContent,
            // ConfigFileTypeEnum.of(configFileType));
            // if (CollectionUtil.isNotEmpty(newValueChangeMap)) {
            // Optional.ofNullable(afterConfigMap).ifPresent(each -> each.putAll(newValueChangeMap));
            // }
            // TODO
            // BootstrapConfigProperties afterConfigProperties = bindProperties(afterConfigMap, context);
            //
            // List<ExecutorProperties> executors = afterConfigProperties.getExecutors();
            // for (ExecutorProperties afterProperties : executors) {
            // String threadPoolId =
            // afterProperties.getThreadPoolId();
            // AgentThreadPoolExecutorHolder holder = AgentThreadPoolInstanceRegistry.getInstance().getHolder(threadPoolId);
            // if (holder.isEmpty() ||
            // holder.getExecutor() == null) {
            // continue;
            // }
            // ExecutorProperties beforeProperties = convert(holder.getProperties());
            // if (!checkConsistency(threadPoolId, beforeProperties, afterProperties)) {
            // continue;
            // }
            // dynamicRefreshPool(beforeProperties, afterProperties);
            // holder.setProperties(failDefaultExecutorProperties(beforeProperties, afterProperties)); // do refresh.
            // ChangeParameterNotifyRequest changeRequest = buildChangeRequest(beforeProperties, afterProperties);
            // LOGGER.info(CHANGE_THREAD_POOL_TEXT, threadPoolId, String.format(CHANGE_DELIMITER,
            // beforeProperties.getCorePoolSize(), changeRequest.getNowCorePoolSize()), String.format(CHANGE_DELIMITER, beforeProperties.getMaximumPoolSize(), changeRequest.getNowMaximumPoolSize()),
            // String.format(CHANGE_DELIMITER, beforeProperties.getQueueCapacity(), changeRequest.getNowQueueCapacity()), String.format(CHANGE_DELIMITER, beforeProperties.getKeepAliveTime(),
            // changeRequest.getNowKeepAliveTime()), String.format(CHANGE_DELIMITER, beforeProperties.getExecuteTimeOut(), changeRequest.getNowExecuteTimeOut()), String.format(CHANGE_DELIMITER,
            // beforeProperties.getRejectedHandler(), changeRequest.getNowRejectedName()), String.format(CHANGE_DELIMITER, beforeProperties.getAllowCoreThreadTimeOut(),
            // changeRequest.getNowAllowsCoreThreadTimeOut()));
            // }
        } catch (Exception ex) {
            LOGGER.error("[Hippo4j-Agent] config mode dynamic refresh failed.", ex);
        }
    }
}
