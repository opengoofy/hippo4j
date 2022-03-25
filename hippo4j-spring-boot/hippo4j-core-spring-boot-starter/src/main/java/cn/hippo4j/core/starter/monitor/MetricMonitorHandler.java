package cn.hippo4j.core.starter.monitor;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;

/**
 * Metric monitor handler.
 *
 * @author chen.ma
 * @date 2022/3/25 20:37
 */
public class MetricMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    public MetricMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        super(threadPoolRunStateHandler);
    }

    @Override
    protected void execute(PoolRunStateInfo poolRunStateInfo) {

    }

    @Override
    public String getType() {
        return "metric";
    }

}
