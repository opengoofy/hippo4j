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

package cn.hippo4j.config.toolkit;

import cn.hippo4j.common.toolkit.StringUtil;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * Env Util.
 */
public class EnvUtil {

    public static final String HIPPO4J_HOME_KEY = "hippo4j.home";

    public static final String STANDALONE_MODE_PROPERTY_NAME = "hippo4j.standalone";

    private static String hippo4jHomePath = null;

    private static Boolean isStandalone = null;

    /**
     * Get hippo4j home.
     *
     * @return
     */
    public static String getHippo4jHome() {
        if (StringUtil.isBlank(hippo4jHomePath)) {
            hippo4jHomePath = System.getProperty(HIPPO4J_HOME_KEY);
            if (StringUtil.isBlank(hippo4jHomePath)) {
                hippo4jHomePath = Paths.get(System.getProperty("user.home"), "hippo4j").toString();
            }
        }
        return hippo4jHomePath;
    }

    /**
     * Standalone mode or not.
     *
     * @return
     */
    public static boolean getStandaloneMode() {
        if (Objects.isNull(isStandalone)) {
            isStandalone = Boolean.getBoolean(STANDALONE_MODE_PROPERTY_NAME);
        }
        return isStandalone;
    }
}
