package cn.hippo4j.console.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Pie chart info.
 *
 * @author chen.ma
 * @date 2021/12/11 14:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieChartInfo {

    /**
     * itemIds
     */
    private List<String> itemIds;

    /**
     * pieDataList
     */
    private List<Map<String, Object>> pieDataList;

}
