package cn.hippo4j.tools.logrecord.service;

/**
 * 函数服务.
 *
 * @author chen.ma
 * @date 2021/10/24 21:30
 */
public interface FunctionService {

    /**
     * 执行.
     *
     * @param functionName
     * @param value
     * @return
     */
    String apply(String functionName, String value);

    /**
     * 是否提前执行.
     *
     * @param functionName
     * @return
     */
    boolean beforeFunction(String functionName);

}
