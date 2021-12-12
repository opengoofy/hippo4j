package cn.hippo4j.config.model.biz.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Monitor active resp dto.
 *
 * @author chen.ma
 * @date 2021/12/12 17:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorActiveRespDTO {

    /**
     * times
     */
    private List<String> times;

    /**
     * poolSizeList
     */
    List<Long> poolSizeList;

    /**
     * activeSizeList
     */
    List<Long> activeSizeList;

    /**
     * queueSizeList
     */
    List<Long> queueSizeList;

    /**
     * completedTaskCountList
     */
    List<Long> completedTaskCountList;

    /**
     * rejectCountList
     */
    List<Long> rejectCountList;

    /**
     * queueRemainingCapacityList
     */
    List<Long> queueRemainingCapacityList;

    /**
     * currentLoadList
     */
    List<Long> currentLoadList;

}
