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
    private List<Long> poolSizeList;

    /**
     * activeSizeList
     */
    private List<Long> activeSizeList;

    /**
     * queueSizeList
     */
    private List<Long> queueSizeList;

    /**
     * completedTaskCountList
     */
    private List<Long> completedTaskCountList;

    /**
     * rejectCountList
     */
    private List<Long> rejectCountList;

    /**
     * queueRemainingCapacityList
     */
    private List<Long> queueRemainingCapacityList;

    /**
     * currentLoadList
     */
    private List<Long> currentLoadList;

    /**
     * queueCapacityList
     */
    private List<Long> queueCapacityList;

}
