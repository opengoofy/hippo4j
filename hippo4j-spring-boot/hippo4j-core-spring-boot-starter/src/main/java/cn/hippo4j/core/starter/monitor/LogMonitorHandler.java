package cn.hippo4j.core.starter.monitor;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Log monitor handler.
 *
 * @author chen.ma
 * @date 2022/3/25 19:22
 */
@Slf4j
public class LogMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    public LogMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        super(threadPoolRunStateHandler);
    }

    @Override
    protected void execute(PoolRunStateInfo poolRunStateInfo) {
        log.info("{}", JSONUtil.toJSONString(poolRunStateInfo));
    }

    @Override
    public String getType() {
        return "log";
    }

}
