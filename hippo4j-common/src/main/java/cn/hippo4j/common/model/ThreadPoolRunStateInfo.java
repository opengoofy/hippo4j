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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Pool run state info.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolRunStateInfo extends ThreadPoolBaseInfo implements Serializable {

    /**
     * currentLoad
     */
    private String currentLoad;

    /**
     * peakLoad
     */
    private String peakLoad;

    /**
     * tpId
     */
    private String tpId;

    /**
     * activeCount
     */
    private Integer activeCount;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * activeSize
     */
    private Integer activeSize;

    /**
     * The maximum number of threads that enter the thread pool at the same time
     */
    private Integer largestPoolSize;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * queueRemainingCapacity
     */
    private Integer queueRemainingCapacity;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * rejectCount
     */
    private Long rejectCount;

    /**
     * host
     */
    private String host;

    /**
     * memoryProportion
     */
    private String memoryProportion;

    /**
     * freeMemory
     */
    private String freeMemory;

    /**
     * clientLastRefreshTime
     */
    private String clientLastRefreshTime;

    /**
     * timestamp
     */
    private Long timestamp;

    public Integer getSimpleCurrentLoad() {
        if (Objects.nonNull(getCurrentLoad())) {
            return Integer.parseInt(getCurrentLoad().replace("%", ""));
        }
        return null;
    }

    public Integer getSimplePeakLoad() {
        if (Objects.nonNull(getPeakLoad())) {
            return Integer.parseInt(getPeakLoad().replace("%", ""));
        }
        return null;
    }
}
