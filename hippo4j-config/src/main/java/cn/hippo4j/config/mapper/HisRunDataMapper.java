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
 *
 * @author chen.ma
 * @date 2021/12/10 21:33
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
    @Select("SELECT " +
            "tenant_id, item_id, tp_id, max(completed_task_count) as max_completed_task_count " +
            "FROM his_run_data " +
            "where timestamp between #{startTime} and #{endTime} " +
            "group by tenant_id, item_id, tp_id " +
            "order by max_completed_task_count desc " +
            "limit 8")
    List<ThreadPoolTaskRanking> queryThreadPoolTaskSumRanking(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Query thread pool task sum ranking.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT " +
            "tenant_id, item_id, tp_id, max(queue_size) as max_queue_size, max(reject_count) as max_reject_count,  max(completed_task_count) as max_completed_task_count " +
            "FROM his_run_data " +
            "where timestamp between #{startTime} and #{endTime} " +
            "group by tenant_id, item_id, tp_id " +
            "order by max_completed_task_count desc " +
            "limit 4")
    List<ThreadPoolTaskRanking> queryThreadPoolMaxRanking(@Param("startTime") Long startTime, @Param("endTime") Long endTime);


    @Data
    class ThreadPoolTaskRanking {

        /**
         * 租户id
         */
        private String tenantId;

        /**
         * 项目id
         */
        private String itemId;

        /**
         * 线程池id
         */
        private String tpId;

        /**
         * 执行任务数
         */
        private Long maxCompletedTaskCount;

        /**
         * 队列元素
         */
        private Long maxQueueSize;

        /**
         * 拒绝次数
         */
        private Long maxRejectCount;

    }
}
