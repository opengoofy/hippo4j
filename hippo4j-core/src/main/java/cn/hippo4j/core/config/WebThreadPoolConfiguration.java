package cn.hippo4j.core.config;

import cn.hippo4j.common.web.executor.JettyWebThreadPoolHandler;
import cn.hippo4j.common.web.executor.TomcatWebThreadPoolHandler;
import cn.hippo4j.common.web.executor.UndertowWebThreadPoolHandler;
import cn.hippo4j.common.web.executor.WebThreadPoolHandlerChoose;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web thread pool configuration.
 *
 * @author chen.ma
 * @date 2022/3/11 19:09
 */
@Configuration
public class WebThreadPoolConfiguration {

    private static final String TOMCAT_SERVLET_WEB_SERVER_FACTORY = "tomcatServletWebServerFactory";

    private static final String JETTY_SERVLET_WEB_SERVER_FACTORY = "JettyServletWebServerFactory";

    private static final String UNDERTOW_SERVLET_WEB_SERVER_FACTORY = "undertowServletWebServerFactory";

    @Bean
    @ConditionalOnBean(name = TOMCAT_SERVLET_WEB_SERVER_FACTORY)
    public TomcatWebThreadPoolHandler tomcatWebThreadPoolHandler() {
        return new TomcatWebThreadPoolHandler();
    }

    @Bean
    @ConditionalOnBean(name = JETTY_SERVLET_WEB_SERVER_FACTORY)
    public JettyWebThreadPoolHandler jettyWebThreadPoolHandler() {
        return new JettyWebThreadPoolHandler();
    }

    @Bean
    @ConditionalOnBean(name = UNDERTOW_SERVLET_WEB_SERVER_FACTORY)
    public UndertowWebThreadPoolHandler undertowWebThreadPoolHandler() {
        return new UndertowWebThreadPoolHandler();
    }

    @Bean
    public WebThreadPoolHandlerChoose webThreadPoolServiceChoose() {
        return new WebThreadPoolHandlerChoose();
    }

}
