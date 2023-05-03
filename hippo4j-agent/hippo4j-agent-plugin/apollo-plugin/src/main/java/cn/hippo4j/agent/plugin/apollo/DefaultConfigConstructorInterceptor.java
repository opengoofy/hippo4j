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

package cn.hippo4j.agent.plugin.apollo;

import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.internals.DefaultConfig;
import com.ctrip.framework.apollo.model.ConfigChange;

import java.util.HashMap;
import java.util.Map;

public class DefaultConfigConstructorInterceptor implements InstanceConstructorInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        // DefaultConfig config = (DefaultConfig) objInst;
        // ConfigChangeListener configChangeListener = configChangeEvent -> {
        // String namespace = this.namespace.replaceAll("." + bootstrapConfigProperties.getConfigFileType().getValue(), "");
        // ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(bootstrapConfigProperties.getConfigFileType().getValue());
        // ConfigFile configFile = ConfigService.getConfigFile(namespace, configFileFormat);
        // Map<String, Object> newChangeValueMap = new HashMap<>();
        // configChangeEvent.changedKeys().stream().filter(each -> each.contains(BootstrapConfigProperties.PREFIX)).forEach(each -> {
        // ConfigChange change = configChangeEvent.getChange(each);
        // String newValue = change.getNewValue();
        // newChangeValueMap.put(each, newValue);
        // });
        // dynamicRefresh(configFile.getContent(), newChangeValueMap);
        // };
        // config.addChangeListener(configChangeListener);
    }
}
