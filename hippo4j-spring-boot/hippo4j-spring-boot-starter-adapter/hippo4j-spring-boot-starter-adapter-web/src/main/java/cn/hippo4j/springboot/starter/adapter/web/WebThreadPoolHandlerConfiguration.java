package cn.hippo4j.springboot.starter.adapter.web;

import cn.hippo4j.adapter.web.TomcatWebThreadPoolHandler;
import cn.hippo4j.adapter.web.WebThreadPoolRunStateHandler;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * Web Thread Pool Handler Configuration
 **/
@Configuration(proxyBeanMethods = false)
public class WebThreadPoolHandlerConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    @ConditionalOnBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
    static class EmbeddedTomcat {

        /**
         * Refer to the Tomcat loading source code .
         * This load is performed if the {@link Tomcat} class exists and
         * the Web embedded server loads the {@link ServletWebServerFactory} top-level interface type at the same time
         */
        @Bean
        public TomcatWebThreadPoolHandler tomcatWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new TomcatWebThreadPoolHandler(webThreadPoolRunStateHandler);
        }
    }
}
