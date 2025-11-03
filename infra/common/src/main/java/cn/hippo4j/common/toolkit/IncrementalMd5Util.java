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
     * Get versioned MD5 based on client semantic version.
     *
     * @param config        thread pool parameter
     * @param clientVersion semantic client version string (can be blank)
     * @return versioned MD5 hash
     */
    public static String getVersionedMd5(ThreadPoolParameter config, String clientVersion) {
        String normalizedVersion = StringUtil.isNotBlank(clientVersion)
                ? clientVersion.trim()
                : VersionUtil.UNKNOWN_VERSION;
        String versionedContent = IncrementalContentUtil.getVersionedContent(config, normalizedVersion);
        String md5 = Md5Util.md5Hex(versionedContent, "UTF-8");
        if (log.isDebugEnabled()) {
            log.debug("ClientVersion={}: Using versioned MD5={}", normalizedVersion, md5);
        }
        return md5;
    }

}
