package cn.hippo4j.core.starter.monitor;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Abstract dynamic thread-pool monitor.
 *
 * @author chen.ma
 * @date 2022/3/25 12:07
 */
@RequiredArgsConstructor
public abstract class AbstractDynamicThreadPoolMonitor implements DynamicThreadPoolMonitor {

    private final ThreadPoolRunStateHandler threadPoolRunStateHandler;

    /**
     * Execute.
     *
     * @param poolRunStateInfo
     */
    protected abstract void execute(PoolRunStateInfo poolRunStateInfo);

    @Override
    public void collect() {
        List<String> listDynamicThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
        for (String each : listDynamicThreadPoolId) {
            PoolRunStateInfo poolRunState = threadPoolRunStateHandler.getPoolRunState(each);
            execute(poolRunState);
        }
    }

}
