package cn.hippo4j.tools.logrecord.service;

/**
 * 函数解析.
 *
 * @author chen.ma
 * @date 2021/10/23 22:40
 */
public interface ParseFunction {

    /**
     * 是否先执行.
     *
     * @return
     */
    default boolean executeBefore() {
        return false;
    }

    /**
     * 函数名称.
     *
     * @return
     */
    String functionName();

    /**
     * 执行.
     *
     * @param value
     * @return
     */
    String apply(String value);

}
