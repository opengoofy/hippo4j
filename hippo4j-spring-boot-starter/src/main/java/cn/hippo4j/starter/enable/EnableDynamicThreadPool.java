package cn.hippo4j.starter.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to activate dynamic threadPool related configuration.
 *
 * @author chen.ma
 * @date 2021/7/8 23:28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({BeforeCheckConfiguration.class, MarkerConfiguration.class})
public @interface EnableDynamicThreadPool {

}
