package cn.hippo4j.springboot.starter.adapter.hystrix;

import cn.hippo4j.adapter.hystrix.HystrixThreadPoolAdapter;
import cn.hippo4j.common.config.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: hippo4j
 * @description:
 * @author: lk
 * @create: 2022-07-15
 **/
@Configuration(proxyBeanMethods = false)
public class HystrixAdapterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public HystrixThreadPoolAdapter hystrixThreadPoolAdapter(){
        return new HystrixThreadPoolAdapter();
    }
}
