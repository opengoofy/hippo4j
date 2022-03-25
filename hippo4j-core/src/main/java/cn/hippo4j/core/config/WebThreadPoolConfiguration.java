package cn.hippo4j.core.config;

import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.executor.web.*;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Web thread pool configuration.
 *
 * @author chen.ma
 * @date 2022/3/11 19:09
 */
@Configuration
@RequiredArgsConstructor
public class WebThreadPoolConfiguration {

    private static final String TOMCAT_SERVLET_WEB_SERVER_FACTORY = "tomcatServletWebServerFactory";

    private static final String JETTY_SERVLET_WEB_SERVER_FACTORY = "JettyServletWebServerFactory";

    private static final String UNDERTOW_SERVLET_WEB_SERVER_FACTORY = "undertowServletWebServerFactory";

    private final ConfigurableEnvironment environment;

    @Bean
    public WebThreadPoolRunStateHandler webThreadPoolRunStateHandler() {
        return new WebThreadPoolRunStateHandler();
    }

    @Bean
    @SuppressWarnings("all")
    public ThreadPoolRunStateHandler threadPoolRunStateHandler(InetUtils hippo4JInetUtils) {
        return new ThreadPoolRunStateHandler(hippo4JInetUtils, environment);
    }

    @Bean
    @ConditionalOnBean(name = TOMCAT_SERVLET_WEB_SERVER_FACTORY)
    public TomcatWebThreadPoolHandler tomcatWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
        return new TomcatWebThreadPoolHandler(webThreadPoolRunStateHandler);
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
