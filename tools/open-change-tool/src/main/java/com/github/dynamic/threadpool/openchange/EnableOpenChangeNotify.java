package com.github.dynamic.threadpool.openchange;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Change timed task.
 *
 * @author chen.ma
 * @date 2021/10/31 13:39
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(OpenChangeNotifyConfig.class)
@Documented
public @interface EnableOpenChangeNotify {
}
