package cn.hippo4j.common.web.executor;

import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;

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
     * Get web thread pool parameter.
     *
     * @return
     */
    PoolParameter getWebThreadPoolParameter();

    /**
     * Update web thread pool.
     *
     * @param poolParameterInfo
     */
    void updateWebThreadPool(PoolParameterInfo poolParameterInfo);

}
