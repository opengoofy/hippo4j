package cn.hippo4j.console.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Line chart info.
 *
 * @author chen.ma
 * @date 2021/12/11 12:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineChartInfo {

    /**
     * completedTaskCounts
     */
    private List<Long> completedTaskCounts;

    /**
     * rejectCounts
     */
    private List<Long> rejectCounts;

}
