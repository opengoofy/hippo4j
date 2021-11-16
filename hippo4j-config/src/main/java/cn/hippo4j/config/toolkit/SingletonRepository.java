package cn.hippo4j.config.toolkit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton repository.
 *
 * @author chen.ma
 * @date 2021/6/24 21:28
 */
public class SingletonRepository<T> {

    public SingletonRepository() {
        shared = new ConcurrentHashMap(1 << 16);
    }

    public T getSingleton(T obj) {
        T previous = shared.putIfAbsent(obj, obj);
        return (null == previous) ? obj : previous;
    }

    public int size() {
        return shared.size();
    }

    public void remove(Object obj) {
        shared.remove(obj);
    }

    private final ConcurrentHashMap<T, T> shared;

    public static class DataIdGroupIdCache {

        public static String getSingleton(String str) {
            return cache.getSingleton(str);
        }

        static SingletonRepository<String> cache = new SingletonRepository<String>();
    }

}
