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

package cn.hippo4j.agent.core.util;

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;

import java.lang.reflect.Field;

public class ExecutorNameUtil {

    private static final ILog LOGGER = LogManager.getLogger(ExecutorNameUtil.class);

    public static boolean isTomcatExecutor(Object threadFactory) {
        try {
            if ("org.apache.tomcat.util.threads.TaskThreadFactory".equals(threadFactory.getClass().getName())) {
                Field namePrefixField = threadFactory.getClass().getDeclaredField(AgentThreadPoolConstants.TOMCAT_NAME_PREFIX);
                namePrefixField.setAccessible(true);
                String namePrefix = (String) namePrefixField.get(threadFactory);
                if (RegexUtil.isTomcatNameMatch(namePrefix)) {
                    return true;
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Fail to put tomcat executor", t);
        }
        return false;
    }
}
