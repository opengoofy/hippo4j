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

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Instance info.
 */
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class InstanceInfo {

    private static final String UNKNOWN = "unknown";

    private String appName = UNKNOWN;

    private String hostName;

    private String groupKey;

    private String port;

    private String instanceId;

    private String ipApplicationName;

    private String clientBasePath;

    private String callBackUrl;

    private String identify;

    private String active;

    private volatile String vipAddress;

    private volatile String secureVipAddress;

    private volatile ActionType actionType;

    private volatile boolean isInstanceInfoDirty = false;

    private volatile Long lastUpdatedTimestamp;

    private volatile Long lastDirtyTimestamp;

    private volatile InstanceStatus status = InstanceStatus.UP;

    private volatile InstanceStatus overriddenStatus = InstanceStatus.UNKNOWN;

    public InstanceInfo() {
        this.lastUpdatedTimestamp = System.currentTimeMillis();
        this.lastDirtyTimestamp = lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp() {
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public Long getLastDirtyTimestamp() {
        return lastDirtyTimestamp;
    }

    public synchronized void setOverriddenStatus(InstanceStatus status) {
        if (this.overriddenStatus != status) {
            this.overriddenStatus = status;
        }
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public synchronized void setIsDirty() {
        isInstanceInfoDirty = true;
        lastDirtyTimestamp = System.currentTimeMillis();
    }

    public synchronized long setIsDirtyWithTime() {
        setIsDirty();
        return lastDirtyTimestamp;
    }

    public synchronized void unsetIsDirty(long unsetDirtyTimestamp) {
        if (lastDirtyTimestamp <= unsetDirtyTimestamp) {
            isInstanceInfoDirty = false;
        }
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    /**
     * Instance status.
     */
    public enum InstanceStatus {

        /**
         * UP
         */
        UP,

        /**
         * DOWN
         */
        DOWN,

        /**
         * STARTING
         */
        STARTING,

        /**
         * OUT_OF_SERVICE
         */
        OUT_OF_SERVICE,

        /**
         * UNKNOWN
         */
        UNKNOWN;

        public static InstanceStatus toEnum(String s) {
            if (s != null) {
                try {
                    return InstanceStatus.valueOf(s.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // ignore and fall through to unknown
                    log.debug("illegal argument supplied to InstanceStatus.valueOf: {}, defaulting to {}", s, UNKNOWN);
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Action type.
     */
    public enum ActionType {
        /**
         * ADDED
         */
        ADDED,

        /**
         * MODIFIED
         */
        MODIFIED,

        /**
         * DELETED
         */
        DELETED
    }

    /**
     * Instance renew.
     */
    @Data
    @Accessors(chain = true)
    public static class InstanceRenew {

        private String appName;

        private String instanceId;

        private String lastDirtyTimestamp;

        private String status;
    }
}
