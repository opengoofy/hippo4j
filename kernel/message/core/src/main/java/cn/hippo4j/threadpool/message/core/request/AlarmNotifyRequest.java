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

package cn.hippo4j.threadpool.message.core.request;

import cn.hippo4j.threadpool.message.core.request.base.BaseNotifyRequest;
import cn.hippo4j.threadpool.message.api.NotifyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Alarm notify request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AlarmNotifyRequest extends BaseNotifyRequest {

    /**
     * Interval
     */
    private Integer interval;

    /**
     * Notify type enum
     */
    private NotifyTypeEnum notifyTypeEnum;

    /**
     * Active
     */
    private String active;

    /**
     * App name
     */
    private String appName;

    /**
     * Identify
     */
    private String identify;

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Pool size
     */
    private Integer poolSize;

    /**
     * Active count
     */
    private Integer activeCount;

    /**
     * Largest pool size
     */
    private Integer largestPoolSize;

    /**
     * Completed task count
     */
    private Long completedTaskCount;

    /**
     * Queue name
     */
    private String queueName;

    /**
     * Capacity
     */
    private Integer capacity;

    /**
     * Queue size
     */
    private Integer queueSize;

    /**
     * Remaining capacity
     */
    private Integer remainingCapacity;

    /**
     * Rejected execution handler name
     */
    private String rejectedExecutionHandlerName;

    /**
     * Reject count num
     */
    private Long rejectCountNum;

    /**
     * Execute time
     */
    private Long executeTime;

    /**
     * Execute timeout
     */
    private Long executeTimeOut;

    /**
     * Execute timeout trace
     */
    private String executeTimeoutTrace;
}
