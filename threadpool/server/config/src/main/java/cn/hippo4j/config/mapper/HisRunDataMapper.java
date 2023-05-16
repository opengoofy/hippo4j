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

package cn.hippo4j.config.mapper;

import cn.hippo4j.config.model.HisRunDataInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * His run data mapper.
 */
@Mapper
public interface HisRunDataMapper extends BaseMapper<HisRunDataInfo> {

    /**
     * Query thread pool task sum ranking.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT "
            + "tenant_id, item_id, tp_id, max(completed_task_count) as max_completed_task_count "
            + "FROM his_run_data "
            + "where timestamp between #{startTime} and #{endTime} "
            + "group by tenant_id, item_id, tp_id "
            + "order by max_completed_task_count desc "
            + "limit 8")
    List<ThreadPoolTaskRanking> queryThreadPoolTaskSumRanking(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Query thread pool task sum ranking.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT "
            + "tenant_id, item_id, tp_id, max(queue_size) as max_queue_size, max(reject_count) as max_reject_count,  max(completed_task_count) as max_completed_task_count "
            + "FROM his_run_data "
            + "where timestamp between #{startTime} and #{endTime} "
            + "group by tenant_id, item_id, tp_id "
            + "order by max_completed_task_count desc "
            + "limit 4")
    List<ThreadPoolTaskRanking> queryThreadPoolMaxRanking(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Thread Pool Task Ranking
     */
    @Data
    class ThreadPoolTaskRanking {

        /**
         * Tenant id
         */
        private String tenantId;

        /**
         * Item id
         */
        private String itemId;

        /**
         * Tp id
         */
        private String tpId;

        /**
         * Max completed task count
         */
        private Long maxCompletedTaskCount;

        /**
         * Max queue size
         */
        private Long maxQueueSize;

        /**
         * Max reject count
         */
        private Long maxRejectCount;
    }
}
