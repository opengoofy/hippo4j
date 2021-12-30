package cn.hippo4j.common.toolkit;

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

}
