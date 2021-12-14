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
     * oneList
     */
    private List<Object> oneList;

    /**
     * twoList
     */
    private List<Object> twoList;

    /**
     * threeList
     */
    private List<Object> threeList;

    /**
     * fourList
     */
    private List<Object> fourList;

}
