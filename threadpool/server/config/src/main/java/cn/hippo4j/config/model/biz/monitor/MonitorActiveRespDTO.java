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

package cn.hippo4j.config.model.biz.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Monitor active resp dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorActiveRespDTO {

    /**
     * Times
     */
    private List<String> times;

    /**
     * Pool size list
     */
    private List<Long> poolSizeList;

    /**
     * Active size list
     */
    private List<Long> activeSizeList;

    /**
     * Queue size list
     */
    private List<Long> queueSizeList;

    /**
     * Range completed task count list
     */
    private List<Long> rangeCompletedTaskCountList;

    /**
     * Completed task count list
     */
    private List<Long> completedTaskCountList;

    /**
     * Range reject count list
     */
    private List<Long> rangeRejectCountList;

    /**
     * Reject count list
     */
    private List<Long> rejectCountList;

    /**
     * Queue remaining capacity list
     */
    private List<Long> queueRemainingCapacityList;

    /**
     * Queue capacity list
     */
    private List<Long> queueCapacityList;
}
