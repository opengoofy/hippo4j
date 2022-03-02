package cn.hippo4j.common.notify;

/**
 * Task trace decorator.
 *
 * @author chen.ma
 * @date 2022/3/2 19:45
 */
public interface TaskTraceBuilder {

    /**
     * Before.
     */
    default void before() {

    }

    /**
     * Trace build.
     *
     * @return
     */
    String traceBuild();

    /**
     * Clear.
     */
    default void clear() {

    }

}
