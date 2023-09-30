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

import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.model.ConfigAllInfo;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hippo4j.common.constant.Constants.LINE_SEPARATOR;
import static cn.hippo4j.common.constant.Constants.WORD_SEPARATOR;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_0;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_1;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_2;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_3;
import static cn.hippo4j.common.constant.MagicNumberConstants.SIZE_4;

/**
 * Md5 config util.
 */
public class Md5ConfigUtil {

    static final char WORD_SEPARATOR_CHAR = (char) 2;

    static final char LINE_SEPARATOR_CHAR = (char) 1;

    private static final int CLIENT_MD5_MAP_INIT_SIZE = 5;
    private static final int CLIENT_MD5_MAP_MAX_SIZE = 10000;
    private static final int CLIENT_MD5_TMP_LIST_INIT_SIZE = 3;
    private static final int CLIENT_MD5_TMP_LIST_MAX_SIZE = 4;
    /**
     * Get thread pool content md5
     *
     * @param config
     * @return
     */
    public static String getTpContentMd5(ConfigAllInfo config) {
        return Md5Util.getTpContentMd5(config);
    }

    /**
     * Compare whether the client Md5 is consistent with the server.
     *
     * @param request
     * @param clientMd5Map
     * @return
     */
    public static List<String> compareMd5(HttpServletRequest request, Map<String, String> clientMd5Map) {
        List<String> changedGroupKeys = new ArrayList();
        clientMd5Map.forEach((key, val) -> {
            String clientIdentify = RequestUtil.getClientIdentify(request);
            boolean isUpdateData = ConfigCacheService.isUpdateData(key, val, clientIdentify);
            if (!isUpdateData) {
                changedGroupKeys.add(key);
            }
        });
        return changedGroupKeys;
    }

    public static Map<String, String> getClientMd5Map(String configKeysString) {
        Map<String, String> md5Map = new HashMap(CLIENT_MD5_MAP_INIT_SIZE);
        if (null == configKeysString || "".equals(configKeysString)) {
            return md5Map;
        }
        int start = 0;
        List<String> tmpList = new ArrayList(CLIENT_MD5_TMP_LIST_INIT_SIZE);
        for (int i = start; i < configKeysString.length(); i++) {
            char c = configKeysString.charAt(i);
            if (c == WORD_SEPARATOR_CHAR) {
                tmpList.add(configKeysString.substring(start, i));
                start = i + 1;
                if (tmpList.size() > CLIENT_MD5_TMP_LIST_MAX_SIZE) {
                    // Malformed message and return parameter error.
                    throw new IllegalArgumentException("invalid protocol,too much key");
                }
            } else if (c == LINE_SEPARATOR_CHAR) {
                String endValue = "";
                if (start + 1 <= i) {
                    endValue = configKeysString.substring(start, i);
                }
                start = i + 1;
                String groupKey = getKey(tmpList.get(INDEX_0), tmpList.get(INDEX_1), tmpList.get(INDEX_2), tmpList.get(INDEX_3));
                groupKey = SingletonRepository.DataIdGroupIdCache.getSingleton(groupKey);
                md5Map.put(groupKey, endValue);
                tmpList.clear();
                // Protect malformed messages
                if (md5Map.size() > CLIENT_MD5_MAP_MAX_SIZE) {
                    throw new IllegalArgumentException("invalid protocol, too much listener");
                }
            }
        }
        return md5Map;
    }

    public static String getKey(String dataId, String group) {
        StringBuilder sb = new StringBuilder();
        GroupKey.urlEncode(dataId, sb);
        sb.append('+');
        GroupKey.urlEncode(group, sb);
        return sb.toString();
    }

    public static String getKey(String dataId, String group, String tenant, String identify) {
        StringBuilder sb = new StringBuilder();
        GroupKey.urlEncode(dataId, sb);
        sb.append('+');
        GroupKey.urlEncode(group, sb);
        if (!StringUtils.isEmpty(tenant)) {
            sb.append('+');
            GroupKey.urlEncode(tenant, sb);
            sb.append("+").append(identify);
        }
        return sb.toString();
    }

    public static String compareMd5ResultString(List<String> changedGroupKeys) throws IOException {
        if (null == changedGroupKeys) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String groupKey : changedGroupKeys) {
            String[] dataIdGroupId = GroupKey.parseKey(groupKey);
            sb.append(dataIdGroupId[0]);
            sb.append(WORD_SEPARATOR);
            sb.append(dataIdGroupId[1]);
            // if have tenant, then set it
            if (dataIdGroupId.length == SIZE_4) {
                if (StringUtil.isNotBlank(dataIdGroupId[2])) {
                    sb.append(WORD_SEPARATOR);
                    sb.append(dataIdGroupId[2]);
                }
            }
            sb.append(LINE_SEPARATOR);
        }
        return URLEncoder.encode(sb.toString(), "UTF-8");
    }
}
