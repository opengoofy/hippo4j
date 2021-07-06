package io.dynamic.threadpool.config.toolkit;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Map Util.
 *
 * @author chen.ma
 * @date 2021/6/23 19:09
 */
public class MapUtil {

    public static Object computeIfAbsent(Map target, Object key, BiFunction mappingFunction, Object param1, Object param2) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(mappingFunction, "mappingFunction");
        Objects.requireNonNull(param1, "param1");
        Objects.requireNonNull(param2, "param2");

        Object val = target.get(key);
        if (val == null) {
            Object ret = mappingFunction.apply(param1, param2);
            target.put(key, ret);
            return ret;
        }
        return val;
    }
}
