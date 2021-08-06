package com.github.dynamic.threadpool.starter.core;

/**
 * Config Adapter.
 *
 * @author chen.ma
 * @date 2021/6/22 21:29
 */
public class ConfigAdapter {

    /**
     * callback Config.
     *
     * @param config
     */
    public void callbackConfig(String config) {
        ThreadPoolDynamicRefresh.refreshDynamicPool(config);
    }

}
