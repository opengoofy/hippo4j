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

import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Incremental content util for thread pool parameter comparison.
 * Supports version compatibility and incremental updates.
 */
@Slf4j
public class IncrementalContentUtil {

    /**
     * Version of the incremental protocol
     */
    public static final int PROTOCOL_VERSION = 2;

    /**
     * Core parameters that affect thread pool behavior
     */
    private static final String[] CORE_PARAMETERS = {
            "coreSize", "maxSize", "queueType", "capacity",
            "keepAliveTime", "rejectedType", "allowCoreThreadTimeOut"
    };

    /**
     * Extended parameters that don't affect core behavior
     */
    private static final String[] EXTENDED_PARAMETERS = {
            "executeTimeOut", "isAlarm", "capacityAlarm", "livenessAlarm"
    };

    /**
     * Get core content for MD5 calculation (only essential parameters)
     *
     * @param parameter thread-pool parameter
     * @return core content string for MD5
     */
    public static String getCoreContent(ThreadPoolParameter parameter) {
        ThreadPoolParameterInfo threadPoolParameterInfo = new ThreadPoolParameterInfo();
        threadPoolParameterInfo.setTenantId(parameter.getTenantId())
                .setItemId(parameter.getItemId())
                .setTpId(parameter.getTpId());
        if (parameter instanceof ThreadPoolParameterInfo) {
            ThreadPoolParameterInfo info = (ThreadPoolParameterInfo) parameter;
            threadPoolParameterInfo.setCorePoolSize(info.corePoolSizeAdapt())
                    .setMaximumPoolSize(info.maximumPoolSizeAdapt());
        } else {
            // Fallback to deprecated methods for non-ThreadPoolParameterInfo implementations
            threadPoolParameterInfo.setCorePoolSize(parameter.getCoreSize())
                    .setMaximumPoolSize(parameter.getMaxSize());
        }

        threadPoolParameterInfo.setQueueType(parameter.getQueueType())
                .setCapacity(parameter.getCapacity())
                .setKeepAliveTime(parameter.getKeepAliveTime())
                .setRejectedType(parameter.getRejectedType())
                .setAllowCoreThreadTimeOut(parameter.getAllowCoreThreadTimeOut());
        return JSONUtil.toJSONString(threadPoolParameterInfo);
    }

    /**
     * Get full content for MD5 calculation (all parameters)
     *
     * @param parameter thread-pool parameter
     * @return full content string for MD5
     */
    public static String getFullContent(ThreadPoolParameter parameter) {
        return ContentUtil.getPoolContent(parameter);
    }

    /**
     * Get incremental content for version compatibility
     *
     * @param parameter thread-pool parameter
     * @param version client version
     * @return incremental content string
     */
    public static String getIncrementalContent(ThreadPoolParameter parameter, int version) {
        if (version >= PROTOCOL_VERSION) {
            return getCoreContent(parameter);
        } else {
            return getFullContent(parameter);
        }
    }

    /**
     * Check if parameters have core changes that require thread pool refresh
     *
     * @param oldParameter old parameter
     * @param newParameter new parameter
     * @return true if core parameters changed
     */
    public static boolean hasCoreChanges(ThreadPoolParameter oldParameter, ThreadPoolParameter newParameter) {
        if (oldParameter == null || newParameter == null) {
            return true;
        }
        // Use adapt methods for ThreadPoolParameterInfo, fallback to deprecated methods for other implementations
        Integer oldCoreSize = (oldParameter instanceof ThreadPoolParameterInfo) ? ((ThreadPoolParameterInfo) oldParameter).corePoolSizeAdapt() : oldParameter.getCoreSize();
        Integer newCoreSize = (newParameter instanceof ThreadPoolParameterInfo) ? ((ThreadPoolParameterInfo) newParameter).corePoolSizeAdapt() : newParameter.getCoreSize();
        Integer oldMaxSize = (oldParameter instanceof ThreadPoolParameterInfo) ? ((ThreadPoolParameterInfo) oldParameter).maximumPoolSizeAdapt() : oldParameter.getMaxSize();
        Integer newMaxSize = (newParameter instanceof ThreadPoolParameterInfo) ? ((ThreadPoolParameterInfo) newParameter).maximumPoolSizeAdapt() : newParameter.getMaxSize();
        return !Objects.equals(oldCoreSize, newCoreSize) ||
                !Objects.equals(oldMaxSize, newMaxSize) ||
                !Objects.equals(oldParameter.getQueueType(), newParameter.getQueueType()) ||
                !Objects.equals(oldParameter.getCapacity(), newParameter.getCapacity()) ||
                !Objects.equals(oldParameter.getKeepAliveTime(), newParameter.getKeepAliveTime()) ||
                !Objects.equals(oldParameter.getRejectedType(), newParameter.getRejectedType()) ||
                !Objects.equals(oldParameter.getAllowCoreThreadTimeOut(), newParameter.getAllowCoreThreadTimeOut());
    }

    /**
     * Check if parameters have extended changes (non-core)
     *
     * @param oldParameter old parameter
     * @param newParameter new parameter
     * @return true if extended parameters changed
     */
    public static boolean hasExtendedChanges(ThreadPoolParameter oldParameter, ThreadPoolParameter newParameter) {
        if (oldParameter == null || newParameter == null) {
            return true;
        }
        return !Objects.equals(oldParameter.getExecuteTimeOut(), newParameter.getExecuteTimeOut()) ||
                !Objects.equals(oldParameter.getIsAlarm(), newParameter.getIsAlarm()) ||
                !Objects.equals(oldParameter.getCapacityAlarm(), newParameter.getCapacityAlarm()) ||
                !Objects.equals(oldParameter.getLivenessAlarm(), newParameter.getLivenessAlarm());
    }

    /**
     * Get parameter changes summary
     *
     * @param oldParameter old parameter
     * @param newParameter new parameter
     * @return changes summary map
     */
    public static Map<String, Object> getChangesSummary(ThreadPoolParameter oldParameter, ThreadPoolParameter newParameter) {
        Map<String, Object> changes = new HashMap<>();
        if (oldParameter == null || newParameter == null) {
            changes.put("type", "full");
            changes.put("reason", "initial_load");
            return changes;
        }
        boolean coreChanges = hasCoreChanges(oldParameter, newParameter);
        boolean extendedChanges = hasExtendedChanges(oldParameter, newParameter);
        if (coreChanges) {
            changes.put("type", "core");
            changes.put("reason", "core_parameters_changed");
        } else if (extendedChanges) {
            changes.put("type", "extended");
            changes.put("reason", "extended_parameters_changed");
        } else {
            changes.put("type", "none");
            changes.put("reason", "no_changes");
        }
        return changes;
    }

    /**
     * Create versioned content for backward compatibility
     *
     * @param parameter thread-pool parameter
     * @param clientVersion client protocol version
     * @return versioned content
     */
    public static String createVersionedContent(ThreadPoolParameter parameter, int clientVersion) {
        Map<String, Object> versionedContent = new HashMap<>();
        versionedContent.put("version", PROTOCOL_VERSION);
        versionedContent.put("clientVersion", clientVersion);
        versionedContent.put("content", getIncrementalContent(parameter, clientVersion));
        versionedContent.put("changes", getChangesSummary(null, parameter));
        return JSONUtil.toJSONString(versionedContent);
    }
}
