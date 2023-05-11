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

package cn.hippo4j.agent.core.boot;

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;
import cn.hippo4j.agent.core.util.ConfigInitializer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class SpringBootConfigInitializer {

    private static final ILog LOG = LogManager.getLogger(SpringBootConfigInitializer.class);

    private static final Set<Class<?>> SPRING_BOOT_CONFIG_LIST = Collections.synchronizedSet(new HashSet<>());

    private static final long MAX_CACHE_TIME = 30L * 60L * 1000L; // half an hour.

    private static final int MAX_CACHE_SIZE = 1000;

    private static long PROPERTIES_LOAD_TIME;

    public static Properties SPRING_PROPERTIES = null;

    private SpringBootConfigInitializer() {

    }

    public static boolean isSpringPropertiesEmpty() {
        return SPRING_PROPERTIES == null || SPRING_PROPERTIES.isEmpty();
    }

    public static synchronized void initializeConfig(SpringBootConfigNode springBootConfig) {
        if (SPRING_PROPERTIES != null) {
            try {
                LOG.info("initialize Spring Config Class {}.", springBootConfig.root());
                ConfigInitializer.initialize(SPRING_PROPERTIES, springBootConfig.root(), true);
            } catch (Throwable e) {
                LOG.error(e, "Failed to set the agent settings {} to Config={} ", SPRING_PROPERTIES, springBootConfig.root());
            }
        }
        boolean isStarting = PROPERTIES_LOAD_TIME == 0L;
        boolean overtime = System.currentTimeMillis() - PROPERTIES_LOAD_TIME > MAX_CACHE_TIME;
        boolean oversize = SPRING_BOOT_CONFIG_LIST.size() > MAX_CACHE_SIZE;
        // avoid memory leak.
        if (isStarting || (!oversize && !overtime)) {
            SPRING_BOOT_CONFIG_LIST.add(springBootConfig.root());
        } else {
            LOG.warn("spirng Config Class is skipped {}.", springBootConfig.root());
        }
    }

    public static synchronized void setSpringProperties(Properties properties) {
        if (properties != null && (SPRING_PROPERTIES == null || properties.size() > SPRING_PROPERTIES.size())) {
            LOG.info("set Spring Config Properties before : {}.", SPRING_PROPERTIES);
            SPRING_PROPERTIES = properties;
            LOG.info("set Spring Config Properties after : {}.", SPRING_PROPERTIES);
            PROPERTIES_LOAD_TIME = System.currentTimeMillis();
        }
        for (Class<?> clazz : SPRING_BOOT_CONFIG_LIST) {
            try {
                LOG.info("initialize Spring Config Class in loop {}.", clazz);
                ConfigInitializer.initialize(SPRING_PROPERTIES, clazz);
            } catch (Throwable e) {
                LOG.error(e, "Failed to set the agent Config={} from settings {}", clazz, properties);
            }
        }
    }
}
