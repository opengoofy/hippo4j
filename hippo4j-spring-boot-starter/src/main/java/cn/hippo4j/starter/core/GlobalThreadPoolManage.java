package cn.hippo4j.starter.core;

import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global threadPool manage.
 *
 * @author chen.ma
 * @date 2021/6/20 15:57
 */
public class GlobalThreadPoolManage {

    /**
     * 动态线程池参数容器
     */
    private static final Map<String, PoolParameter> POOL_PARAMETER = new ConcurrentHashMap();

    /**
     * 动态线程池包装容器
     */
    private static final Map<String, DynamicThreadPoolWrapper> EXECUTOR_MAP = new ConcurrentHashMap();

    /**
     * 获取动态线程池包装类.
     *
     * @param threadPoolId
     * @return
     */
    public static DynamicThreadPoolWrapper getExecutorService(String threadPoolId) {
        return EXECUTOR_MAP.get(threadPoolId);
    }

    /**
     * 获取动态线程池参数.
     *
     * @param threadPoolId
     * @return
     */
    public static PoolParameter getPoolParameter(String threadPoolId) {
        return POOL_PARAMETER.get(threadPoolId);
    }

    /**
     * 注册动态线程池包装以及参数.
     *
     * @param threadPoolId
     * @param poolParameter
     * @param executor
     */
    public static void register(String threadPoolId, PoolParameter poolParameter, DynamicThreadPoolWrapper executor) {
        registerPool(threadPoolId, executor);
        registerPoolParameter(threadPoolId, poolParameter);
    }

    /**
     * 注册动态线程池.
     *
     * @param threadPoolId
     * @param executor
     */
    public static void registerPool(String threadPoolId, DynamicThreadPoolWrapper executor) {
        EXECUTOR_MAP.put(threadPoolId, executor);
    }

    /**
     * 注册动态线程池参数.
     *
     * @param threadPoolId
     * @param poolParameter
     */
    public static void registerPoolParameter(String threadPoolId, PoolParameter poolParameter) {
        POOL_PARAMETER.put(threadPoolId, poolParameter);
    }

    /**
     * 获取动态线程池标识集合.
     *
     * @return
     */
    public static List<String> listThreadPoolId() {
        return Lists.newArrayList(POOL_PARAMETER.keySet());
    }

    /**
     * 获取动态线程池数量.
     * 数据在项目最初启动的时候可能不准确, 因为是异步进行注册.
     *
     * @return
     */
    public static Integer getThreadPoolNum() {
        return listThreadPoolId().size();
    }

}
