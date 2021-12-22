package cn.hippo4j.common.toolkit;

import java.util.Iterator;
import java.util.Map;

/**
 * Collection util.
 *
 * @author chen.ma
 * @date 2021/12/22 20:43
 */
public class CollectionUtil {

    /**
     * Get first.
     *
     * @param iterable
     * @param <T>
     * @return
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        Iterator<T> iterator;
        if (iterable != null && (iterator = iterable.iterator()) != null && iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }

    /**
     * Is not empty.
     *
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && map.isEmpty() == false;
    }

}
