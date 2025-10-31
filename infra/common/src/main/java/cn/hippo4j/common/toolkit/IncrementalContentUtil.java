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

import cn.hippo4j.common.model.IncrementalFieldMetadataProvider;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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

    private static final List<String> IDENTIFIER_FIELDS = Collections.unmodifiableList(Arrays.asList("tenantId", "itemId", "tpId"));

    private static final List<String> CORE_PARAMETER_LIST = Collections.unmodifiableList(Arrays.asList(CORE_PARAMETERS));

    private static final String FIELD_VERSION_METADATA_KEY = "fieldVersionMetadata";

    private static final String FIELD_METADATA_VERSION_KEY = "fieldMetadataVersion";

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
        LinkedHashMap<String, Object> raw = JSONUtil.parseObject(fullContent, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        if (raw == null) {
            return fullContent;
        }
        String normalizedClientVersion = StringUtil.isNotBlank(clientVersion)
                ? clientVersion.trim()
                : VersionUtil.resolveSemanticVersionForProtocol(protocolVersion);
        Map<String, String> fieldRules = resolveFieldRules(parameter, raw);
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
            if (!filtered.containsKey(field) && shouldIncludeField(field, normalizedClientVersion, fieldRules)) {
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
    private static boolean shouldIncludeField(String field, String clientVersion, Map<String, String> fieldRules) {
        String minVersion = fieldRules.get(field);
        if (StringUtil.isBlank(minVersion)) {
            return false;
        }
        String effectiveClientVersion = StringUtil.isBlank(clientVersion) ? VersionUtil.UNKNOWN_VERSION : clientVersion;
        return VersionUtil.isVersionGreaterOrEqual(effectiveClientVersion, minVersion);
    }

    /**
     * Resolve field-level version rules by combining default baseline, runtime metadata from the
     * parameter object, and metadata embedded in the JSON payload. Fields without explicit metadata
     * are assigned a default minimum version based on the current protocol.
     *
     * @param parameter thread pool parameter (may carry metadata)
     * @param raw       parsed JSON payload (may contain fieldVersionMetadata)
     * @return mapping of field name to minimum semantic version
     */
    private static Map<String, String> resolveFieldRules(ThreadPoolParameter parameter, Map<String, Object> raw) {
        Map<String, String> fieldRules = new LinkedHashMap<>();
        // Identifier and core fields visible to all clients (since version 1.0.0)
        IDENTIFIER_FIELDS.forEach(field -> fieldRules.put(field, VersionUtil.UNKNOWN_VERSION));
        CORE_PARAMETER_LIST.forEach(field -> fieldRules.put(field, VersionUtil.UNKNOWN_VERSION));

        // Merge metadata from parameter object (e.g., Server-side configuration)
        mergeFieldMetadata(fieldRules, extractMetadataFromParameter(parameter));
        // Merge metadata from JSON payload (e.g., Client receiving Server's dynamic metadata)
        mergeFieldMetadata(fieldRules, extractMetadataFromPayload(raw));

        // Assign default version to unconfigured fields (prevents old clients from seeing new fields)
        String defaultVisibleVersion = VersionUtil.resolveSemanticVersionForProtocol(PROTOCOL_VERSION);
        raw.keySet().forEach(field -> {
            if (!fieldRules.containsKey(field)) {
                fieldRules.put(field, defaultVisibleVersion);
            }
        });

        return fieldRules;
    }

    /**
     * Extract field version metadata from the parameter object if it implements
     * {@link IncrementalFieldMetadataProvider}. This is typically used on the Server side where
     * configuration objects can dynamically declare which fields were introduced in which version.
     *
     * @param parameter thread pool parameter
     * @return field-to-version mapping, or empty map if not available
     */
    private static Map<String, String> extractMetadataFromParameter(ThreadPoolParameter parameter) {
        if (parameter instanceof IncrementalFieldMetadataProvider) {
            Map<String, String> metadata = ((IncrementalFieldMetadataProvider) parameter).getFieldVersionMetadata();
            if (metadata != null && !metadata.isEmpty()) {
                Map<String, String> copied = new LinkedHashMap<>();
                metadata.forEach((field, version) -> {
                    if (StringUtil.isNotBlank(field) && StringUtil.isNotBlank(version)) {
                        copied.put(field, version.trim());
                    }
                });
                return copied;
            }
        }
        return Collections.emptyMap();
    }

    /**
     * Extract field version metadata from the JSON payload and remove metadata keys from the raw map
     * so they do not participate in MD5 calculation. This is typically used on the Client side to
     * receive dynamic metadata from the Server.
     *
     * @param raw parsed JSON map (will be modified: metadata keys removed)
     * @return field-to-version mapping extracted from the payload, or empty map if not present
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> extractMetadataFromPayload(Map<String, Object> raw) {
        Object metadataObject = raw.remove(FIELD_VERSION_METADATA_KEY);
        raw.remove(FIELD_METADATA_VERSION_KEY);
        if (metadataObject instanceof Map<?, ?>) {
            Map<String, String> metadata = new LinkedHashMap<>();
            ((Map<?, ?>) metadataObject).forEach((key, value) -> {
                if (key == null || value == null) {
                    return;
                }
                String field = String.valueOf(key);
                String version = String.valueOf(value).trim();
                if (StringUtil.isNotBlank(field) && StringUtil.isNotBlank(version)) {
                    metadata.put(field, version);
                }
            });
            return metadata;
        }
        return Collections.emptyMap();
    }

    /**
     * Merge additional field version metadata into the target map. Existing entries in the target
     * will be overwritten by additions. This enables layered metadata resolution (base → parameter → payload).
     *
     * @param target    target map to merge into
     * @param additions additional metadata to merge (may be null or empty)
     */
    private static void mergeFieldMetadata(Map<String, String> target, Map<String, String> additions) {
        if (additions == null || additions.isEmpty()) {
            return;
        }
        additions.forEach((field, version) -> {
            if (StringUtil.isNotBlank(field) && StringUtil.isNotBlank(version)) {
                target.put(field, version.trim());
            }
        });
    }
}
