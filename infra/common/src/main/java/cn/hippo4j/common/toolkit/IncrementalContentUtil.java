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
     * Default server version used when Implementation-Version cannot be read from MANIFEST.MF.
     * This typically occurs in development/test environments before packaging.
     */
    private static final String DEFAULT_SERVER_VERSION = "2.0.0";

    private static final String[] CORE_IDS = {
            "tenantId", "itemId", "tpId"
    };

    /**
     * Core thread pool parameters that must be visible to all client versions.
     * These fields are essential for thread pool operation and should not be modified.
     */
    private static final String[] CORE_PARAMETERS = {
            "coreSize", "maxSize", "queueType", "capacity",
            "keepAliveTime", "rejectedType"
    };

    private static final List<String> IDENTIFIER_FIELDS = Collections.unmodifiableList(Arrays.asList(CORE_IDS));

    private static final List<String> CORE_PARAMETER_LIST = Collections.unmodifiableList(Arrays.asList(CORE_PARAMETERS));

    /**
     * Build version-aware content string for MD5 calculation.
     * Fields introduced in newer versions are automatically excluded for older clients.
     *
     * @param parameter     thread pool parameter
     * @param clientVersion client semantic version (e.g., "1.5.0", null defaults to UNKNOWN_VERSION)
     * @return filtered content string based on client version
     */
    public static String getVersionedContent(ThreadPoolParameter parameter, String clientVersion) {
        String fullContent = ContentUtil.getPoolContent(parameter);
        LinkedHashMap<String, Object> raw = JSONUtil.parseObject(fullContent, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        if (raw == null) {
            return fullContent;
        }
        String normalizedClientVersion = StringUtil.isNotBlank(clientVersion)
                ? clientVersion.trim()
                : VersionUtil.UNKNOWN_VERSION;
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
     * Check if a field should be included for the given client version.
     * Returns false if the field requires a higher version than the client supports.
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
     * Build field-to-version mapping by priority:
     * 1. Core/identifier fields → UNKNOWN_VERSION (visible to all clients)
     * 2. Registry fields → pre-registered version in FieldVersionInitializer
     * 3. Runtime metadata → for testing or manual override
     * 4. Unregistered fields → current server version (with warning)
     *
     * @param parameter thread pool parameter
     * @param raw       parsed JSON map
     * @return field name to minimum required version mapping
     */
    private static Map<String, String> resolveFieldRules(ThreadPoolParameter parameter, Map<String, Object> raw) {
        Map<String, String> fieldRules = new LinkedHashMap<>();

        // Core and identifier fields (visible to all versions)
        IDENTIFIER_FIELDS.forEach(field -> fieldRules.put(field, VersionUtil.UNKNOWN_VERSION));
        CORE_PARAMETER_LIST.forEach(field -> fieldRules.put(field, VersionUtil.UNKNOWN_VERSION));

        // Load from global registry
        Map<String, String> registryVersions = FieldVersionRegistry.getAllFieldVersions();
        registryVersions.forEach((field, version) -> {
            if (!fieldRules.containsKey(field)) {
                fieldRules.put(field, version);
            }
        });

        // Handle unregistered fields
        String currentServerVersion = getCurrentServerVersion();
        raw.keySet().forEach(field -> {
            if (!fieldRules.containsKey(field)) {
                log.warn("Unregistered field '{}' detected. Binding to current server version '{}'. " +
                        "To fix: Add to FieldVersionInitializer or CORE_PARAMETERS if essential.",
                        field, currentServerVersion);
                fieldRules.put(field, currentServerVersion);
            }
        });

        return fieldRules;
    }

    /**
     * Get current server version from package metadata (set by Maven during build).
     * Falls back to DEFAULT_SERVER_VERSION if not available (e.g., in development/test environments).
     *
     * @return server semantic version string
     */
    private static String getCurrentServerVersion() {
        Package pkg = IncrementalContentUtil.class.getPackage();
        if (pkg != null) {
            String implVersion = pkg.getImplementationVersion();
            if (StringUtil.isNotBlank(implVersion)) {
                return implVersion.trim();
            }
        }
        log.warn("Unable to read Implementation-Version from MANIFEST.MF. " +
                "Falling back to default version '{}'. " +
                "Please ensure maven-jar-plugin is properly configured.", DEFAULT_SERVER_VERSION);
        return DEFAULT_SERVER_VERSION;
    }
}
