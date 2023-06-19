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

import cn.hippo4j.common.constant.Constants;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;

/**
 * Group key.
 * Refer to com.alibaba.nacos.client.config.common.GroupKey:<br>
 */
public class GroupKey {

    public static String getKey(String dataId, String group) {
        return getKey(dataId, group, "");
    }

    public static String getKey(String dataId, String group, String datumStr) {
        return doGetKey(dataId, group, datumStr);
    }

    public static String getKey(String... params) {
        StringBuilder groupKey = new StringBuilder();
        groupKey.append(params[0]).append(GROUP_KEY_DELIMITER);
        for (int i = 1; i < params.length - 1; i++) {
            groupKey.append(params[i]).append(GROUP_KEY_DELIMITER);
        }
        groupKey.append(params[params.length - 1]);
        return groupKey.toString();
    }

    public static String getKeyTenant(String dataId, String group, String tenant) {
        return doGetKey(dataId, group, tenant);
    }

    private static String doGetKey(String dataId, String group, String datumStr) {
        StringBuilder sb = new StringBuilder();
        urlEncode(dataId, sb);
        sb.append(GROUP_KEY_DELIMITER);
        urlEncode(group, sb);
        if (!StringUtil.isEmpty(datumStr)) {
            sb.append(GROUP_KEY_DELIMITER);
            urlEncode(datumStr, sb);
        }
        return sb.toString();
    }

    public static String[] parseKey(String groupKey) {
        return groupKey.split(Constants.GROUP_KEY_DELIMITER_TRANSLATION);
    }

    public static void urlEncode(String str, StringBuilder sb) {
        for (int idx = 0; idx < str.length(); ++idx) {
            char c = str.charAt(idx);
            if ('+' == c) {
                sb.append("%2B");
            } else if ('%' == c) {
                sb.append("%25");
            } else {
                sb.append(c);
            }
        }
    }
}
