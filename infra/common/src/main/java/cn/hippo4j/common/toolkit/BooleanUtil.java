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

package cn.hippo4j.common.toolkit;

import java.util.HashSet;
import java.util.Set;

/**
 * Boolean util.
 */
public class BooleanUtil {

    private static final Set<String> TREE_SET = new HashSet(3);

    static {
        TREE_SET.add("true");
        TREE_SET.add("yes");
        TREE_SET.add("1");
    }

    /**
     * To boolean.
     *
     * @param valueStr
     * @return
     */
    public static boolean toBoolean(String valueStr) {
        if (StringUtil.isNotBlank(valueStr)) {
            valueStr = valueStr.trim().toLowerCase();
            return TREE_SET.contains(valueStr);
        }
        return false;
    }

    /**
     * Is true.
     *
     * @param bool
     * @return
     */
    public static boolean isTrue(Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }
}
