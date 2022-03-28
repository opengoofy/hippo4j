package cn.hippo4j.console.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Tenant chart.
 *
 * @author chen.ma
 * @date 2021/12/11 16:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantChart {

    /**
     * tenantCharts
     */
    private List<Map<String, Object>> tenantCharts;

}
