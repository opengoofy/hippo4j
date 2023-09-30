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

package cn.hippo4j.core.extension.initialize;

import cn.hippo4j.common.propertie.EnvironmentProperties;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.threadpool.alarm.api.ThreadPoolCheckAlarm;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Hippo-4j dynamic thread-pool initializer.
 * <p>
 * Compatible with SpringBoot Starter and Agent mode.
 */
public class Hippo4jDynamicThreadPoolInitializer implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // Environment initialize
        ConfigurableEnvironment environment = ApplicationContextHolder.getBean(ConfigurableEnvironment.class);
        EnvironmentProperties.active = environment.getProperty("spring.profiles.active", "UNKNOWN");
        EnvironmentProperties.itemId = environment.getProperty("spring.dynamic.thread-pool.item-id", "");
        EnvironmentProperties.applicationName = environment.getProperty("spring.application.name", "");
        EnvironmentProperties.checkStateInterval = environment.getProperty("spring.dynamic.thread-pool.check-state-interval", Long.class, 5L);
        IdentifyUtil.getIdentify();
        // Check alarm
        ThreadPoolCheckAlarm threadPoolCheckAlarm = ApplicationContextHolder.getBean(ThreadPoolCheckAlarm.class);
        threadPoolCheckAlarm.scheduleExecute();
    }
}
