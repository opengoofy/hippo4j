package cn.hippo4j.console.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Char info.
 *
 * @author chen.ma
 * @date 2021/11/10 21:06
 */
@Data
@Accessors(chain = true)
public class ChartInfo {

    /**
     * 租户统计
     */
    private Integer tenantCount;

    /**
     * 项目统计
     */
    private Integer itemCount;

    /**
     * 线程池统计
     */
    private Integer threadPoolCount;

    /**
     * 线程池实例统计
     */
    private Integer threadPoolInstanceCount;

}
