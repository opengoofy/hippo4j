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

package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.notify.NotifyPlatformEnum;
import cn.hippo4j.common.notify.ThreadPoolNotifyAlarm;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.starter.config.ExecutorProperties;
import cn.hippo4j.core.starter.config.NotifyPlatformProperties;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.core.starter.config.BootstrapCoreProperties.PREFIX;

/**
 * Bootstrap core properties binder adapt.
 *
 * @author chen.ma
 * @date 2022/3/9 19:04
 */
public class BootstrapCorePropertiesBinderAdapt {

    /**
     * Bootstrap core properties binder.
     *
     * @param configInfo
     * @param bootstrapCoreProperties
     * @return
     */
    public static BootstrapCoreProperties bootstrapCorePropertiesBinder(Map<Object, Object> configInfo, BootstrapCoreProperties bootstrapCoreProperties) {
        BootstrapCoreProperties bindableCoreProperties = null;
        try {
            ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
            Binder binder = new Binder(sources);
            bindableCoreProperties = binder.bind(PREFIX, Bindable.ofInstance(bootstrapCoreProperties)).get();
        } catch (Exception ex) {
            try {
                Class.forName("org.springframework.boot.context.properties.bind.Binder");
            } catch (ClassNotFoundException notEx) {
                bindableCoreProperties = adapt(configInfo);
            }
        }
        return bindableCoreProperties;
    }

    /**
     * 此处采用硬编码适配低版本 SpringBoot 1.5.x, 如果有更好的方法进行逻辑转换的话, 欢迎 PR.
     *
     * @param configInfo
     * @return
     */
    @Deprecated
    private static BootstrapCoreProperties adapt(Map<Object, Object> configInfo) {
        BootstrapCoreProperties bindableCoreProperties;
        try {
            // filter
            Map<Object, Object> targetMap = Maps.newHashMap();
            configInfo.forEach((key, val) -> {
                boolean containFlag = key != null
                        && StringUtil.isNotBlank((String) key)
                        && (((String) key).indexOf(PREFIX + ".executors") != -1
                                || ((String) key).indexOf(PREFIX + ".notify-platforms") != -1
                                || ((String) key).indexOf(PREFIX + ".notifyPlatforms") != -1);
                if (containFlag) {
                    String targetKey = key.toString().replace(PREFIX + ".", "");
                    targetMap.put(targetKey, val);
                }
            });
            // convert
            List<ExecutorProperties> executorPropertiesList = Lists.newArrayList();
            List<NotifyPlatformProperties> notifyPropertiesList = Lists.newArrayList();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                Map<String, Object> executorSingleMap = Maps.newHashMap();
                Map<String, Object> platformSingleMap = Maps.newHashMap();
                Map<String, Object> notifySingleMap = Maps.newHashMap();

                for (Map.Entry entry : targetMap.entrySet()) {
                    String key = entry.getKey().toString();
                    if (key.indexOf("executors[" + i + "].") != -1) {
                        if (key.indexOf("executors[" + i + "].notify.") != -1) {
                            key = key.replace("executors[" + i + "].notify.", "");
                            String[] notifyKeySplit = key.split("-");
                            if (notifyKeySplit != null && notifyKeySplit.length > 0) {
                                key = key.replace("-", "_");
                            }
                            notifySingleMap.put(key, entry.getValue());
                        } else {
                            key = key.replace("executors[" + i + "].", "");

                            String[] keySplit = key.split("-");
                            if (keySplit != null && keySplit.length > 0) {
                                key = key.replace("-", "_");
                            }
                            executorSingleMap.put(key, entry.getValue());
                        }
                    }
                    if (key.indexOf("notify-platforms[" + i + "].") != -1 || key.indexOf("notifyPlatforms[" + i + "].") != -1) {
                        if (key.indexOf("notify-platforms[" + i + "].") != -1) {
                            key = key.replace("notify-platforms[" + i + "].", "");
                        } else {
                            key = key.replace("notifyPlatforms[" + i + "].", "");
                        }
                        String[] keySplit = key.split("-");
                        if (keySplit != null && keySplit.length > 0) {
                            key = key.replace("-", "_");
                        }
                        platformSingleMap.put(key, entry.getValue());
                    }
                }
                if (CollectionUtil.isEmpty(executorSingleMap) && CollectionUtil.isEmpty(platformSingleMap)) {
                    break;
                }
                if (CollectionUtil.isNotEmpty(executorSingleMap)) {
                    ExecutorProperties executorProperties = BeanUtil.mapToBean(executorSingleMap, ExecutorProperties.class, true, CopyOptions.create());
                    if (executorProperties != null) {
                        if (CollectionUtil.isNotEmpty(notifySingleMap)) {
                            ThreadPoolNotifyAlarm alarm = BeanUtil.mapToBean(notifySingleMap, ThreadPoolNotifyAlarm.class, true, CopyOptions.create());
                            Map<String, String> notifyReceivesMap = Maps.newHashMap();
                            for (NotifyPlatformEnum value : NotifyPlatformEnum.values()) {
                                Object receives = targetMap.get("executors[" + i + "].notify.receives." + value.name());
                                if (receives != null && StringUtil.isNotBlank((String) receives)) {
                                    notifyReceivesMap.put(value.name(), (String) receives);
                                }
                            }
                            alarm.setReceives(notifyReceivesMap);
                            executorProperties.setNotify(alarm);
                        }
                        executorPropertiesList.add(executorProperties);
                    }
                }
                if (CollectionUtil.isNotEmpty(platformSingleMap)) {
                    NotifyPlatformProperties notifyPlatformProperties = BeanUtil.mapToBean(platformSingleMap, NotifyPlatformProperties.class, true, CopyOptions.create());
                    if (notifyPlatformProperties != null) {
                        notifyPropertiesList.add(notifyPlatformProperties);
                    }
                }
            }
            bindableCoreProperties = new BootstrapCoreProperties();
            bindableCoreProperties.setExecutors(executorPropertiesList);
            bindableCoreProperties.setNotifyPlatforms(notifyPropertiesList);
        } catch (Exception ex) {
            throw ex;
        }
        return bindableCoreProperties;
    }
}
