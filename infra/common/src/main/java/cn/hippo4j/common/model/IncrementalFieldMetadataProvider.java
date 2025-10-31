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

package cn.hippo4j.common.model;

import java.util.Collections;
import java.util.Map;

/**
 * Optional provider for field-version metadata.
 * <p>
 * Thread pool parameter models implementing this interface can explicitly declare the
 * relationship between fields and the protocol versions that understand them. This allows
 * the incremental content builder to omit unsupported fields for legacy clients and avoid
 * unnecessary refresh loops triggered by unknown data.
 */
public interface IncrementalFieldMetadataProvider {

    /**
     * Return a mapping of field name to the minimum protocol version that can observe it.
     *
     * @return field -> minimum semantic version; fields not present fall back to defaults
     */
    default Map<String, String> getFieldVersionMetadata() {
        return Collections.emptyMap();
    }

    /**
     * Optional version string of the metadata definition, useful for caching or diagnostics.
     *
     * @return metadata version identifier, or {@code null} if not set
     */
    default String getFieldMetadataVersion() {
        return null;
    }
}
