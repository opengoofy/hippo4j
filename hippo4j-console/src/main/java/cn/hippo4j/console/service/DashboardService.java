package cn.hippo4j.console.service;

import cn.hippo4j.console.model.*;

/**
 * Dashboard service.
 *
 * @author chen.ma
 * @date 2021/11/1 21:06
 */
public interface DashboardService {

    /**
     * Get chart info.
     *
     * @return
     */
    ChartInfo getChartInfo();

    /**
     * Fet line chat info.
     *
     * @return
     */
    LineChartInfo getLineChatInfo();

    /**
     * Get tenant chart.
     *
     * @return
     */
    TenantChart getTenantChart();

    /**
     * Get pie chart.
     *
     * @return
     */
    PieChartInfo getPieChart();

    /**
     * Get ranking chart.
     *
     * @return
     */
    RankingChart getRankingChart();

}
