package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程池全局管理
 *
 * @author chen.ma
 * @date 2021/6/20 15:57
 */
public class GlobalThreadPoolManage {

    private static final Map<String, DynamicThreadPoolWrap> EXECUTOR_MAP = new ConcurrentHashMap();

    public static DynamicThreadPoolWrap getExecutorService(String tpId) {
        return EXECUTOR_MAP.get(tpId);
    }

    public static void register(String tpId, DynamicThreadPoolWrap executor) {
        EXECUTOR_MAP.put(tpId, executor);
    }

    public static void remove(String tpId) {
        EXECUTOR_MAP.remove(tpId);
    }
}