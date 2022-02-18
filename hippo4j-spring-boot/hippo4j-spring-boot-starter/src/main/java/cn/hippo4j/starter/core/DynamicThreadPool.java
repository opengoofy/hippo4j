package cn.hippo4j.starter.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Dynamic thread pool.
 *
 * @author chen.ma
 * @date 2021/10/13 21:50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicThreadPool {

}
