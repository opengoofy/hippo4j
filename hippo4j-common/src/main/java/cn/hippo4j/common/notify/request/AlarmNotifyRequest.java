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

package cn.hippo4j.common.notify.request;

import cn.hippo4j.common.notify.NotifyTypeEnum;
import cn.hippo4j.common.notify.request.base.BaseNotifyRequest;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Alarm notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 19:41
 */
@Data
@Accessors(chain = true)
public class AlarmNotifyRequest extends BaseNotifyRequest {

    /**
     * interval
     */
    private Integer interval;

    /**
     * notifyTypeEnum
     */
    private NotifyTypeEnum notifyTypeEnum;

    /**
     * active
     */
    private String active;

    /**
     * appName
     */
    private String appName;

    /**
     * identify
     */
    private String identify;

    /**
     * corePoolSize
     */
    private Integer corePoolSize;

    /**
     * maximumPoolSize
     */
    private Integer maximumPoolSize;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * activeCount
     */
    private Integer activeCount;

    /**
     * largestPoolSize
     */
    private Integer largestPoolSize;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * queueName
     */
    private String queueName;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * remainingCapacity
     */
    private Integer remainingCapacity;

    /**
     * rejectedExecutionHandlerName
     */
    private String rejectedExecutionHandlerName;

    /**
     * rejectCountNum
     */
    private Long rejectCountNum;

    /**
     * executeTime
     */
    private Long executeTime;

    /**
     * executeTimeOut
     */
    private Long executeTimeOut;

    /**
     * executeTimeoutTrace
     */
    private String executeTimeoutTrace;
}
