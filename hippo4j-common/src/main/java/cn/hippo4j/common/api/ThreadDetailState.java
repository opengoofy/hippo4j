package cn.hippo4j.common.api;

import cn.hippo4j.common.model.ThreadDetailStateInfo;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Get thread status in thread pool.
 *
 * @author chen.ma
 * @date 2022/1/9 12:47
 */
public interface ThreadDetailState {

    /**
     * Get thread status in thread pool.
     *
     * @param threadPoolId
     * @return
     */
    List<ThreadDetailStateInfo> getThreadDetailStateInfo(String threadPoolId);

    /**
     * Get thread status in thread pool.
     *
     * @param threadPoolExecutor
     * @return
     */
    List<ThreadDetailStateInfo> getThreadDetailStateInfo(ThreadPoolExecutor threadPoolExecutor);

}
