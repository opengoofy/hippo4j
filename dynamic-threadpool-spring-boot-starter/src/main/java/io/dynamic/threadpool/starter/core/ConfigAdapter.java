package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.starter.core.ThreadPoolDynamicRefresh;

/**
 * ConfigAdapter.
 *
 * @author chen.ma
 * @date 2021/6/22 21:29
 */
public class ConfigAdapter {

    /**
     * 回调修改线程池配置
     *
     * @param config
     */
    public void callbackConfig(String config) {
        ThreadPoolDynamicRefresh.refreshDynamicPool(config);
    }
}
