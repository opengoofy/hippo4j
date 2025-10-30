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
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

    private static final List<String> IDENTIFIER_FIELDS = Collections.unmodifiableList(Arrays.asList("tenantId", "itemId", "tpId"));

    private static final List<String> CORE_PARAMETER_LIST = Collections.unmodifiableList(Arrays.asList(CORE_PARAMETERS));

    private static final List<String> EXTENDED_PARAMETER_LIST = Collections.unmodifiableList(Arrays.asList(EXTENDED_PARAMETERS));

    /**
     * Mapping of field name to the minimum protocol version that should observe it. Clients whose
     * protocol version is lower than the mapped value will skip the field when generating MD5s, so
     * they never refresh on data they do not understand.
     */
    private static final Map<String, Integer> FIELD_MIN_PROTOCOL_VERSION;

    static {
        Map<String, Integer> fieldVersion = new HashMap<>();
        // Identifiers are required regardless of protocol version.
        IDENTIFIER_FIELDS.forEach(field -> fieldVersion.put(field, 1));
        // Core parameters affect pool behaviour, therefore protocol v1 clients must see them.
        CORE_PARAMETER_LIST.forEach(field -> fieldVersion.put(field, 1));

        // Initial new/extended fields with the next protocol version so current clients (v2)
        // automatically skip them when generating MD5 values. Once a field is ready to be exposed
        // to protocol v2 (or higher) clients, simply lower its minimum version accordingly.
        EXTENDED_PARAMETER_LIST.forEach(field -> fieldVersion.put(field, PROTOCOL_VERSION + 1));

        FIELD_MIN_PROTOCOL_VERSION = Collections.unmodifiableMap(fieldVersion);
    }

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
                .setTpId(parameter.getTpId())
                .setCorePoolSize(getCorePoolSize(parameter))
                .setMaximumPoolSize(getMaximumPoolSize(parameter))
                .setQueueType(parameter.getQueueType())
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
     * Build content string according to client protocol version. Fields introduced in newer protocol
     * versions will be excluded automatically for older clients to avoid unnecessary refresh.
     *
     * @param parameter      thread-pool parameter
     * @param protocolVersion client protocol version
     * @param clientVersion   semantic client version (optional, reserved for fine-grained rules)
     * @return version-aware content string
     */
    public static String getVersionedContent(ThreadPoolParameter parameter, int protocolVersion, String clientVersion) {
        String fullContent = getFullContent(parameter);
        if (protocolVersion < PROTOCOL_VERSION) {
            return fullContent;
        }
        LinkedHashMap<String, Object> raw = JSONUtil.parseObject(fullContent, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        if (raw == null) {
            return fullContent;
        }
        int normalizedProtocol = Math.max(protocolVersion, PROTOCOL_VERSION);
        LinkedHashMap<String, Object> filtered = new LinkedHashMap<>();
        for (String field : IDENTIFIER_FIELDS) {
            if (raw.containsKey(field)) {
                filtered.put(field, raw.get(field));
            }
        }
        for (String field : CORE_PARAMETER_LIST) {
            if (raw.containsKey(field)) {
                filtered.put(field, raw.get(field));
            }
        }
        raw.forEach((field, value) -> {
            if (!filtered.containsKey(field) && shouldIncludeField(field, normalizedProtocol)) {
                filtered.put(field, value);
            }
        });
        return JSONUtil.toJSONString(filtered);
    }

    /**
     * Get core pool size with version compatibility handling
     *
     * @param parameter thread pool parameter
     * @return core pool size
     */
    private static Integer getCorePoolSize(ThreadPoolParameter parameter) {
        if (parameter instanceof ThreadPoolParameterInfo) {
            return ((ThreadPoolParameterInfo) parameter).corePoolSizeAdapt();
        }
        return parameter.getCoreSize();
    }

    /**
     * Get maximum pool size with version compatibility handling
     *
     * @param parameter thread pool parameter
     * @return maximum pool size
     */
    private static Integer getMaximumPoolSize(ThreadPoolParameter parameter) {
        if (parameter instanceof ThreadPoolParameterInfo) {
            return ((ThreadPoolParameterInfo) parameter).maximumPoolSizeAdapt();
        }
        return parameter.getMaxSize();
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
        return !Objects.equals(getCorePoolSize(oldParameter), getCorePoolSize(newParameter)) ||
                !Objects.equals(getMaximumPoolSize(oldParameter), getMaximumPoolSize(newParameter)) ||
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
     * Decide whether the given field should be included when generating MD5 for a client that uses
     * the specified protocol version. If the field requires a higher protocol, it will be ignored
     * so older clients remain unaware of unsupported parameters.
     */
    private static boolean shouldIncludeField(String field, int protocolVersion) {
        int minProtocol = FIELD_MIN_PROTOCOL_VERSION.getOrDefault(field, Integer.MAX_VALUE);
        return protocolVersion >= minProtocol;
    }
}
