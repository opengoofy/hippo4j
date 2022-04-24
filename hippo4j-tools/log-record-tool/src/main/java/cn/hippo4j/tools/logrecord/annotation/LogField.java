package cn.hippo4j.tools.logrecord.annotation;

/**
 * 日志字段, 用于标记需要比较的实体属性.
 *
 * @author chen.ma
 * @date 2021/10/23 21:29
 */
public @interface LogField {

    /**
     * 字段名称
     *
     * @return
     */
    String name();

}
