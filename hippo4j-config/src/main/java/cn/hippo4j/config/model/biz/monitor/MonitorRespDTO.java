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

import lombok.Data;

/**
 * Monitor resp dto.
 *
 * @author chen.ma
 * @date 2021/12/10 20:23
 */
@Data
public class MonitorRespDTO {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 实例id
     */
    private String instanceId;

    /**
     * 已完成任务计数
     */
    private String completedTaskCount;

    /**
     * 线程池id
     */
    private String tpId;

    /**
     * 当前负载
     */
    private String currentLoad;

    /**
     * 峰值负载
     */
    private String peakLoad;

    /**
     * 线程数
     */
    private String poolSize;

    /**
     * 活跃线程数
     */
    private String activeSize;

    /**
     * 队列容量
     */
    private String queueCapacity;

    /**
     * 队列元素
     */
    private String queueSize;

    /**
     * 队列剩余容量
     */
    private String queueRemainingCapacity;

    /**
     * 拒绝次数
     */
    private String rejectCount;

}
