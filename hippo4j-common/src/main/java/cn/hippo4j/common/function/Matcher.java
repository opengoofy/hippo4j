package cn.hippo4j.common.function;

/**
 * Matcher.
 *
 * @author chen.ma
 * @date 2022/1/9 13:29
 */
@FunctionalInterface
public interface Matcher<T> {

    /**
     * Match.
     *
     * @param t
     * @return
     */
    boolean match(T t);

}
