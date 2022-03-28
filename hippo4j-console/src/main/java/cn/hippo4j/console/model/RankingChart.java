package cn.hippo4j.console.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Ranking chart.
 *
 * @author chen.ma
 * @date 2021/12/11 19:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingChart {

    /**
     * rankingChartInfoList
     */
    private List<RankingChartInfo> rankingChartInfoList;

    @Data
    public static class RankingChartInfo {

        /**
         * groupKey
         */
        private String groupKey;

        /**
         * completedTaskCount
         */
        private Long maxCompletedTaskCount;

        /**
         * inst
         */
        private Integer inst;

    }

}
