package cn.hippo4j.starter.core;

/**
 * Config adapter.
 *
 * @author chen.ma
 * @date 2021/6/22 21:29
 */
public class ConfigAdapter {

    /**
     * Callback Config.
     *
     * @param config
     */
    public void callbackConfig(String config) {
        ThreadPoolDynamicRefresh.refreshDynamicPool(config);
    }

}
