package cn.hippo4j.starter.core;

/**
 * ThreadPool subscribe callback.
 *
 * @author chen.ma
 * @date 2021/6/22 20:26
 */
public interface ThreadPoolSubscribeCallback {

    /**
     * Callback.
     *
     * @param config
     */
    void callback(String config);

}
