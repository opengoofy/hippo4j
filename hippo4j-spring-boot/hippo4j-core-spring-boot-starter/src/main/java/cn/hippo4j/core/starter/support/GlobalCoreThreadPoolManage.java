package cn.hippo4j.core.starter.support;

import cn.hippo4j.core.starter.config.ExecutorProperties;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Global core thread pool manage.
 *
 * @author chen.ma
 * @date 2022/2/26 19:47
 */
public class GlobalCoreThreadPoolManage {

    private static final Map<String, ExecutorProperties> EXECUTOR_PROPERTIES = Maps.newConcurrentMap();

    /**
     * Get properties.
     *
     * @param threadPoolId
     * @return
     */
    public static ExecutorProperties getProperties(String threadPoolId) {
        return EXECUTOR_PROPERTIES.get(threadPoolId);
    }

    /**
     * Register.
     *
     * @param threadPoolId
     * @param executorProperties
     */
    public static void register(String threadPoolId, ExecutorProperties executorProperties) {
        EXECUTOR_PROPERTIES.put(threadPoolId, executorProperties);
    }

    /**
     * Refresh.
     *
     * @param threadPoolId
     * @param executorProperties
     */
    public static void refresh(String threadPoolId, ExecutorProperties executorProperties) {
        EXECUTOR_PROPERTIES.put(threadPoolId, executorProperties);
    }

}
