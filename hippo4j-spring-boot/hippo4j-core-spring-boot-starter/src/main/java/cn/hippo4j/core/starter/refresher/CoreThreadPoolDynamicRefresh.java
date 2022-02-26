package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import lombok.AllArgsConstructor;

/**
 * Core thread pool dynamic refresh.
 *
 * @author chen.ma
 * @date 2022/2/26 12:32
 */
@AllArgsConstructor
public class CoreThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh {

    private final ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler;

    @Override
    public void dynamicRefresh(String content) {

    }

}
