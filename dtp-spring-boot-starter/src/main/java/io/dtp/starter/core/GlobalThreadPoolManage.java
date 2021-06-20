package io.dtp.starter.core;

import io.dtp.starter.wrap.DynamicThreadPoolWrap;

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

    public static DynamicThreadPoolWrap getExecutorService(String name) {
        return EXECUTOR_MAP.get(name);
    }

    public static void register(String name, DynamicThreadPoolWrap executor) {
        EXECUTOR_MAP.put(name, executor);
    }

    public static void remove(String name) {
        EXECUTOR_MAP.remove(name);
    }
}