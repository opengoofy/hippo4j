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

package cn.hippo4j.core.springboot.starter.config;

import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Objects;

/**
 * Executor properties.
 *
 * @author chen.ma
 * @date 2022/2/25 00:40
 */
@Data
@Accessors(chain = true)
public class ExecutorProperties {

    /**
     * threadPoolId
     */
    private String threadPoolId;

    /**
     * corePoolSize
     */
    private Integer corePoolSize;

    /**
     * maximumPoolSize
     */
    private Integer maximumPoolSize;

    /**
     * queueCapacity
     */
    private Integer queueCapacity;

    /**
     * blockingQueue
     */
    private String blockingQueue;

    /**
     * rejectedHandler
     */
    private String rejectedHandler;

    /**
     * keepAliveTime
     */
    private Long keepAliveTime;

    /**
     * executeTimeOut
     */
    private Long executeTimeOut;

    /**
     * allowCoreThreadTimeOut
     */
    private Boolean allowCoreThreadTimeOut;

    /**
     * threadNamePrefix
     */
    private String threadNamePrefix;

    /**
     * Notify
     */
    private ThreadPoolNotifyAlarm notify;

    public Map<String, String> receives() {
        return Objects.isNull(this.notify) || this.notify.getReceives() == null ? Maps.newHashMap() : this.notify.getReceives();
    }
}
