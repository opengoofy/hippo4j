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

package cn.hippo4j.config.springboot.starter.refresher;

import com.tencent.polaris.configuration.api.core.ConfigFileService;
import com.tencent.polaris.configuration.api.core.ConfigKVFile;
import com.tencent.polaris.configuration.api.core.ConfigKVFileChangeListener;
import com.tencent.polaris.configuration.api.core.ConfigPropertyChangeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Polaris refresher handler.
 */
@RequiredArgsConstructor
public class PolarisRefresherHandler extends AbstractConfigThreadPoolDynamicRefresh {

    private final ConfigFileService configFileService;

    private static final String POLARIS_NAMESPACE = "${spring.dynamic.thread-pool.polaris.namespace:dev}";

    private static final String POLARIS_FILE_GROUP = "${spring.dynamic.thread-pool.polaris.file.group:dynamic}";

    private static final String POLARIS_FILE_NAME = "${spring.dynamic.thread-pool.polaris.file.name:root/bootstrap.yaml}";

    private static final String POLARIS_FILE_TYPE = "${spring.dynamic.thread-pool.polaris.file.type:properties}";

    @Value(POLARIS_NAMESPACE)
    private String namespace;

    @Value(POLARIS_FILE_GROUP)
    private String fileGroup;

    @Value(POLARIS_FILE_NAME)
    private String fileName;

    @Override
    public void registerListener() {
        ConfigKVFile configFile = getConfigKVFile();
        configFile.addChangeListener((ConfigKVFileChangeListener) event -> {
            String content = configFile.getContent();
            Map<String, Object> newChangeValueMap = new HashMap<>();
            for (String key : event.changedKeys()) {
                ConfigPropertyChangeInfo changeInfo = event.getChangeInfo(key);
                newChangeValueMap.put(key, changeInfo.getNewValue());
            }
            dynamicRefresh(content, newChangeValueMap);
        });
    }

    private ConfigKVFile getConfigKVFile() {
        return Objects.equals(POLARIS_FILE_TYPE, "yaml") ? configFileService.getConfigYamlFile(namespace, fileGroup, fileName)
                : configFileService.getConfigPropertiesFile(namespace, fileGroup, fileName);
    }

}
