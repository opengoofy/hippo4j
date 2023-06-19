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

package cn.hippo4j.threadpool.dynamic.mode.config.refresher;

import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigParserHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * Abstract config thread-pool dynamic refresh.
 */
@Slf4j
public abstract class AbstractConfigThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh {

    @Override
    public void dynamicRefresh(String configFileType, String configContent) {
        dynamicRefresh(configFileType, configContent, null);
    }

    @Override
    public void dynamicRefresh(String configFileType, String configContent, Map<String, Object> newValueChangeMap) {
        try {
            Map<Object, Object> configInfo = ConfigParserHandler.getInstance().parseConfig(configContent, configFileType);
            if (CollectionUtil.isNotEmpty(newValueChangeMap)) {
                Optional.ofNullable(configInfo).ifPresent(each -> each.putAll(newValueChangeMap));
            }
            BootstrapPropertiesInterface bootstrapProperties = buildBootstrapProperties(configInfo);
            AbstractSubjectCenter.notify(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH, () -> bootstrapProperties);
        } catch (Exception ex) {
            log.error("Hippo4j config mode dynamic refresh failed.", ex);
        }
    }
}
