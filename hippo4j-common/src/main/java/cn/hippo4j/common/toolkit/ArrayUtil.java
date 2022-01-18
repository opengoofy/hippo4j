package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.function.Matcher;

/**
 * Array util.
 *
 * @author chen.ma
 * @date 2021/12/30 21:42
 */
public class ArrayUtil {

    /**
     * Is empty.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Is not empty.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }


    /**
     * First match.
     *
     * @param matcher
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T firstMatch(Matcher<T> matcher, T... array) {
        if (!isEmpty(array)) {
            for (final T val : array) {
                if (matcher.match(val)) {
                    return val;
                }
            }
        }

        return null;
    }

}
