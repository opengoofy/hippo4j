package io.dynamict.hreadpool.common.executor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Thread Pool Manager.
 *
 * @author chen.ma
 * @date 2021/6/23 18:36
 */
public class ThreadPoolManager {

    private Map<String, Map<String, Set<ExecutorService>>> resourcesManager;

    private Map<String, Object> lockers = new ConcurrentHashMap(8);

    private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return INSTANCE;
    }

    public void register(String namespace, String group, ExecutorService executor) {
        if (!resourcesManager.containsKey(namespace)) {
            synchronized (this) {
                lockers.put(namespace, new Object());
            }
        }
        final Object monitor = lockers.get(namespace);
        synchronized (monitor) {
            Map<String, Set<ExecutorService>> map = resourcesManager.get(namespace);
            if (map == null) {
                map = new HashMap(8);
                map.put(group, new HashSet());
                map.get(group).add(executor);
                resourcesManager.put(namespace, map);
                return;
            }
            if (!map.containsKey(group)) {
                map.put(group, new HashSet());
            }
            map.get(group).add(executor);
        }
    }
}
