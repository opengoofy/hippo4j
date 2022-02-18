package cn.hippo4j.starter.toolkit;

import cn.hippo4j.common.config.ApplicationContextHolder;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * {@link DynamicThreadPoolAnnotationUtil} 是为了适配低版本 SpringBoot.
 *
 * <p>Spring version >= 5.2.0 下述方法才是有效的, 等同于 SpringBoot version 2.2.0.RELEASE
 * {@link ListableBeanFactory#findAnnotationOnBean(java.lang.String, java.lang.Class)}
 *
 * <p>但这不是一个优雅的实现方式, 因为其中用到了很多强类型转换, 不确定后续 Spring 升级是否会受影响.
 * 不过, 可以确定的是 Spring version < 5.3.14 是没问题的, 等同于 SpringBoot version < 2.6.2
 *
 * @author chen.ma
 * @date 2022/1/5 21:15
 */
public class DynamicThreadPoolAnnotationUtil {

    /**
     * 根据 {@param beanName} 查询注解 {@param annotationType} 是否存在.
     *
     * @param beanName
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        AbstractApplicationContext context = (AbstractApplicationContext) ApplicationContextHolder.getInstance();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        A annotation = Optional.ofNullable(beanFactory)
                .map(each -> (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName))
                .map(definition -> definition.getResolvedFactoryMethod())
                .map(factoryMethod -> AnnotationUtils.getAnnotation(factoryMethod, annotationType))
                .orElse(null);

        return annotation;
    }

}
