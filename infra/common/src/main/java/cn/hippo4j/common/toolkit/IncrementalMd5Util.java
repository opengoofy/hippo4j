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
import lombok.extern.slf4j.Slf4j;

/**
 * Incremental MD5 util for thread pool parameter comparison.
 * Supports version compatibility and reduces unnecessary refreshes.
 */
@Slf4j
public class IncrementalMd5Util {

    /**
     * Get core MD5 for essential parameters only
     *
     * @param config thread pool parameter
     * @return core MD5 hash
     */
    public static String getCoreMd5(ThreadPoolParameter config) {
        String coreContent = IncrementalContentUtil.getCoreContent(config);
        return Md5Util.md5Hex(coreContent, "UTF-8");
    }

    /**
     * Get full MD5 for all parameters (legacy compatibility)
     *
     * @param config thread pool parameter
     * @return full MD5 hash
     */
    public static String getFullMd5(ThreadPoolParameter config) {
        return Md5Util.getTpContentMd5(config);
    }

    /**
     * Get versioned MD5 based on client version
     *
     * @param config thread pool parameter
     * @param clientVersion client protocol version
     * @return versioned MD5 hash
     */
    public static String getVersionedMd5(ThreadPoolParameter config, int clientVersion) {
        if (clientVersion >= IncrementalContentUtil.PROTOCOL_VERSION) {
            String coreMd5 = getCoreMd5(config);
            if (log.isDebugEnabled()) {
                log.debug("Protocol v{}: Using incremental MD5 (core parameters only), MD5={}", clientVersion, coreMd5);
            }
            return coreMd5;
        } else {
            String fullMd5 = getFullMd5(config);
            if (log.isDebugEnabled()) {
                log.debug("Protocol v{}: Using full MD5 (all parameters), MD5={}", clientVersion, fullMd5);
            }
            return fullMd5;
        }
    }

    /**
     * Compare MD5 with version support
     *
     * @param oldConfig old configuration
     * @param newConfig new configuration
     * @param clientVersion client version
     * @return true if configurations are different
     */
    public static boolean isDifferent(ThreadPoolParameter oldConfig, ThreadPoolParameter newConfig, int clientVersion) {
        if (oldConfig == null || newConfig == null) {
            return true;
        }
        String oldMd5 = getVersionedMd5(oldConfig, clientVersion);
        String newMd5 = getVersionedMd5(newConfig, clientVersion);
        boolean different = !oldMd5.equals(newMd5);
        if (different) {
            log.debug("Configuration changed - Old MD5: {}, New MD5: {}, Client Version: {}",
                    oldMd5, newMd5, clientVersion);
        }
        return different;
    }

    /**
     * Check if only extended parameters changed (non-core)
     *
     * @param oldConfig old configuration
     * @param newConfig new configuration
     * @return true if only extended parameters changed
     */
    public static boolean onlyExtendedChanged(ThreadPoolParameter oldConfig, ThreadPoolParameter newConfig) {
        if (oldConfig == null || newConfig == null) {
            return false;
        }
        boolean coreChanged = IncrementalContentUtil.hasCoreChanges(oldConfig, newConfig);
        boolean extendedChanged = IncrementalContentUtil.hasExtendedChanges(oldConfig, newConfig);
        return !coreChanged && extendedChanged;
    }

    /**
     * Get change type for logging and monitoring
     *
     * @param oldConfig old configuration
     * @param newConfig new configuration
     * @return change type string
     */
    public static String getChangeType(ThreadPoolParameter oldConfig, ThreadPoolParameter newConfig) {
        if (oldConfig == null || newConfig == null) {
            return "INITIAL";
        }
        boolean coreChanged = IncrementalContentUtil.hasCoreChanges(oldConfig, newConfig);
        boolean extendedChanged = IncrementalContentUtil.hasExtendedChanges(oldConfig, newConfig);
        if (coreChanged) {
            return "CORE";
        } else if (extendedChanged) {
            return "EXTENDED";
        } else {
            return "NONE";
        }
    }
}
