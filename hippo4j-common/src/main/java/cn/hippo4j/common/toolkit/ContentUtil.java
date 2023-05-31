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
import cn.hippo4j.common.model.ThreadPoolParameter;

/**
 * Content util.
 */
public class ContentUtil {

    /**
     * Get pool content.
     *
     * @param parameter thread-pool parameter
     * @return dynamic thread-pool content str
     */
    public static String getPoolContent(ThreadPoolParameter parameter) {
        String getPoolContent = String.format("{\"tenantId\":\"%s\",\"itemId\":\"%s\",\"tpId\":" +
                "\"%s\",\"queueType\":%d,\"capacity\":%d,\"keepAliveTime\":%d,\"rejectedType\":%d,\"isAlarm\"" +
                ":%d,\"capacityAlarm\":%d,\"livenessAlarm\":%d,\"allowCoreThreadTimeOut\":%d}", parameter.getTenantId(),
                parameter.getItemId(), parameter.getTpId(), parameter.getQueueType(), parameter.getCapacity(),
                parameter.getKeepAliveTime(), parameter.getRejectedType(), parameter.getIsAlarm(),parameter.getCapacityAlarm(), 
                parameter.getLivenessAlarm(), parameter.getAllowCoreThreadTimeOut());
        return getPoolContent;
    }

    /**
     * Get group key.
     *
     * @param parameter thread-pool parameter
     * @return group key
     */
    public static String getGroupKey(ThreadPoolParameter parameter) {
        return StringUtil.createBuilder()
                .append(parameter.getTpId())
                .append(Constants.GROUP_KEY_DELIMITER)
                .append(parameter.getItemId())
                .append(Constants.GROUP_KEY_DELIMITER)
                .append(parameter.getTenantId())
                .toString();
    }

    /**
     * Get group key.
     *
     * @param parameters thread-pool parameters
     * @return group key
     */
    public static String getGroupKey(String... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i]);
            if (i < parameters.length - 1) {
                stringBuilder.append(Constants.GROUP_KEY_DELIMITER);
            }
        }
        return stringBuilder.toString();
    }
}
