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

    private static final Map<String, PoolParameter> POOL_PARAMETER = new ConcurrentHashMap();

    private static final Map<String, DynamicThreadPoolWrapper> EXECUTOR_MAP = new ConcurrentHashMap();

    public static DynamicThreadPoolWrapper getExecutorService(String tpId) {
        return EXECUTOR_MAP.get(tpId);
    }

    public static PoolParameter getPoolParameter(String tpId) {
        return POOL_PARAMETER.get(tpId);
    }

    public static void register(String tpId, PoolParameter poolParameter, DynamicThreadPoolWrapper executor) {
        registerPool(tpId, executor);
        registerPoolParameter(tpId, poolParameter);
    }

    public static void registerPool(String tpId, DynamicThreadPoolWrapper executor) {
        EXECUTOR_MAP.put(tpId, executor);
    }

    public static void registerPoolParameter(String tpId, PoolParameter poolParameter) {
        POOL_PARAMETER.put(tpId, poolParameter);
    }

    public static List<String> listThreadPoolId() {
        return Lists.newArrayList(POOL_PARAMETER.keySet());
    }

}
