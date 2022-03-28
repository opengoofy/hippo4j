package cn.hippo4j.core.executor.web;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;

import java.util.concurrent.Executor;

/**
 * Web thread pool service.
 *
 * @author chen.ma
 * @date 2022/1/19 20:51
 */
public interface WebThreadPoolService {

    /**
     * Get web thread pool.
     *
     * @return Tomcat、Jetty、Undertow ThreadPoolExecutor
     */
    Executor getWebThreadPool();

    /**
     * Simple info.
     *
     * @return
     */
    PoolBaseInfo simpleInfo();

    /**
     * Get web thread pool parameter.
     *
     * @return
     */
    PoolParameter getWebThreadPoolParameter();

    /**
     * Get web run state info.
     *
     * @return
     */
    PoolRunStateInfo getWebRunStateInfo();

    /**
     * Update web thread pool.
     *
     * @param poolParameterInfo
     */
    void updateWebThreadPool(PoolParameterInfo poolParameterInfo);

}
