package io.dynamic.threadpool.server.toolkit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton Repository.
 *
 * @author chen.ma
 * @date 2021/6/24 21:28
 */
public class SingletonRepository<T> {

    public SingletonRepository() {
        // Initializing size 2^16, the container itself use about 50K of memory, avoiding constant expansion
        shared = new ConcurrentHashMap(1 << 16);
    }

    public T getSingleton(T obj) {
        T previous = shared.putIfAbsent(obj, obj);
        return (null == previous) ? obj : previous;
    }

    public int size() {
        return shared.size();
    }

    /**
     * Be careful use.
     */
    public void remove(Object obj) {
        shared.remove(obj);
    }

    private final ConcurrentHashMap<T, T> shared;


    /**
     * Cache of DataId and Group.
     */
    public static class DataIdGroupIdCache {

        public static String getSingleton(String str) {
            return cache.getSingleton(str);
        }

        static SingletonRepository<String> cache = new SingletonRepository<String>();
    }
}
