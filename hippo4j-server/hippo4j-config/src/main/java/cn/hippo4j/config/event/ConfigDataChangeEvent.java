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

package cn.hippo4j.config.event;

import org.springframework.util.StringUtils;

/**
 * Config data change event.
 */
public class ConfigDataChangeEvent extends AbstractEvent {

    public final String tenantId;

    public final String itemId;

    public final String tpId;

    public final long lastModifiedTs;

    public ConfigDataChangeEvent(String tenantId, String itemId, String tpId, Long gmtModified) {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(itemId) || StringUtils.isEmpty(tpId)) {
            throw new IllegalArgumentException("DataId is null or group is null");
        }
        this.tenantId = tenantId;
        this.itemId = itemId;
        this.tpId = tpId;
        this.lastModifiedTs = gmtModified;
    }
}
