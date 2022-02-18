package cn.hippo4j.starter.core;

import java.util.concurrent.Executor;

/**
 * Listener.
 *
 * @author chen.ma
 * @date 2021/6/22 20:20
 */
public interface Listener {

    /**
     * Get executor.
     *
     * @return
     */
    Executor getExecutor();

    /**
     * Receive config info.
     *
     * @param configInfo
     */
    void receiveConfigInfo(String configInfo);

}
