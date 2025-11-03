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

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global registry for field version metadata.
 * Maintains persistent mapping of field names to their introduction versions.
 * This ensures that field versions remain stable across server restarts and upgrades.
 */
@Slf4j
public class FieldVersionRegistry {

    /**
     * Global field version mapping: fieldName -> introducedVersion
     * Uses ConcurrentHashMap for thread-safe access without external synchronization.
     */
    private static final Map<String, String> FIELD_VERSIONS = new ConcurrentHashMap<>();

    /**
     * Register a field with its introduction version.
     * Uses putIfAbsent to preserve the first registered version (earliest version wins).
     *
     * @param fieldName field name
     * @param version   semantic version when this field was introduced (e.g., "2.0.0")
     */
    public static void registerField(String fieldName, String version) {
        if (StringUtil.isBlank(fieldName) || StringUtil.isBlank(version)) {
            return;
        }
        String existing = FIELD_VERSIONS.putIfAbsent(fieldName.trim(), version.trim());
        if (existing == null) {
            log.debug("Registered field version: {} -> {}", fieldName, version);
        }
    }

    /**
     * Register multiple fields with their introduction versions.
     *
     * @param fieldVersions mapping of field name to introduction version
     */
    public static void registerFields(Map<String, String> fieldVersions) {
        if (fieldVersions == null || fieldVersions.isEmpty()) {
            return;
        }
        fieldVersions.forEach(FieldVersionRegistry::registerField);
    }

    /**
     * Get the introduction version for a field.
     *
     * @param fieldName field name
     * @return introduction version, or null if not registered
     */
    public static String getFieldVersion(String fieldName) {
        if (StringUtil.isBlank(fieldName)) {
            return null;
        }
        return FIELD_VERSIONS.get(fieldName.trim());
    }

    /**
     * Get all registered field versions (read-only view).
     *
     * @return unmodifiable map of field name to introduction version
     */
    public static Map<String, String> getAllFieldVersions() {
        return Collections.unmodifiableMap(FIELD_VERSIONS);
    }

    /**
     * Check if a field is registered.
     *
     * @param fieldName field name
     * @return true if the field has a registered version
     */
    public static boolean isFieldRegistered(String fieldName) {
        return StringUtil.isNotBlank(fieldName) && FIELD_VERSIONS.containsKey(fieldName.trim());
    }

    /**
     * Clear all registered field versions (for testing only).
     */
    static void clearForTest() {
        FIELD_VERSIONS.clear();
    }
}
