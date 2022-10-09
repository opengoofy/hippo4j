package cn.hippo4j.auth.config;

import cn.hippo4j.auth.filter.RewriteUserInfoApiFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RewriteUserInfoApiFilter> userInfoApiFilterRegistrationBean() {
        FilterRegistrationBean<RewriteUserInfoApiFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RewriteUserInfoApiFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}