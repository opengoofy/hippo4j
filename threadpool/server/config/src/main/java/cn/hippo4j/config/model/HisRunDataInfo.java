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

package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * His run data info.
 */
@Data
@TableName("his_run_data")
public class HisRunDataInfo {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 线程池id
     */
    private String tpId;

    /**
     * 当前负载
     */
    private Long currentLoad;

    /**
     * 峰值负载
     */
    private Long peakLoad;

    /**
     * 线程数
     */
    private Long poolSize;

    /**
     * 活跃线程数
     */
    private Long activeSize;

    /**
     * 队列容量
     */
    private Long queueCapacity;

    /**
     * 队列元素
     */
    private Long queueSize;

    /**
     * 队列剩余容量
     */
    private Long queueRemainingCapacity;

    /**
     * 已完成任务计数
     */
    private Long completedTaskCount;

    /**
     * 拒绝次数
     */
    private Long rejectCount;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
