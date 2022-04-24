package cn.hippo4j.tools.logrecord.annotation;

import cn.hippo4j.tools.logrecord.enums.LogRecordTypeEnum;

import java.lang.annotation.*;

/**
 * 日志记录注解.
 *
 * @author chen.ma
 * @date 2021/10/23 21:29
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /**
     * 业务前缀
     *
     * @return
     */
    String prefix() default "";

    /**
     * 操作日志文本模版
     *
     * @return
     */
    String success();

    /**
     * 操作日志失败的文本
     *
     * @return
     */
    String fail() default "";

    /**
     * 操作人
     *
     * @return
     */
    String operator() default "";

    /**
     * 业务码
     *
     * @return
     */
    String bizNo();

    /**
     * 日志详情
     *
     * @return
     */
    String detail() default "";

    /**
     * 日志种类
     *
     * @return
     */
    String category();

    /**
     * 记录类型
     *
     * @return
     */
    LogRecordTypeEnum recordType() default LogRecordTypeEnum.COMPLETE;

    /**
     * 记录日志条件
     *
     * @return
     */
    String condition() default "";

}
