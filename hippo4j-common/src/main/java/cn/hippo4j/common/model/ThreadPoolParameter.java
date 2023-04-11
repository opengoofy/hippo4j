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

/**
 * Thread-pool parameter.
 */
public interface ThreadPoolParameter {

    /**
     * Get tenant id
     *
     * @return
     */
    String getTenantId();

    /**
     * Get item id
     *
     * @return
     */
    String getItemId();

    /**
     * Get thread-pool id
     *
     * @return
     */
    String getTpId();

    /**
     * Get core size
     *
     * @return
     */
    Integer getCoreSize();

    /**
     * Get max size
     *
     * @return
     */
    Integer getMaxSize();

    /**
     * Get queue type
     *
     * @return
     */
    Integer getQueueType();

    /**
     * Get capacity
     *
     * @return
     */
    Integer getCapacity();

    /**
     * Get keep alive time
     *
     * @return
     */
    Long getKeepAliveTime();

    /**
     * Get execute time out
     *
     * @return
     */
    Long getExecuteTimeOut();

    /**
     * Get rejected type
     *
     * @return
     */
    Integer getRejectedType();

    /**
     * Get is alarm
     *
     * @return
     */
    Integer getIsAlarm();

    /**
     * Get capacity alarm
     *
     * @return
     */
    Integer getCapacityAlarm();

    /**
     * Get liveness alarm
     *
     * @return
     */
    Integer getLivenessAlarm();

    /**
     * Get allow core thread timeOut
     *
     * @return
     */
    Integer getAllowCoreThreadTimeOut();
}
