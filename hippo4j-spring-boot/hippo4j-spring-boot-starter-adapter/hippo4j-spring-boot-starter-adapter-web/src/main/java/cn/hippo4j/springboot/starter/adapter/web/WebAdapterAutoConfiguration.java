/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.springboot.starter.adapter.web;

import cn.hippo4j.adapter.web.*;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import javax.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;

/**
 * Web adapter auto configuration.
 */
@Configuration
@RequiredArgsConstructor
public class WebAdapterAutoConfiguration {

    private static final String JETTY_SERVLET_WEB_SERVER_FACTORY = "JettyServletWebServerFactory";

    private static final String UNDERTOW_SERVLET_WEB_SERVER_FACTORY = "undertowServletWebServerFactory";

    private final ConfigurableEnvironment environment;

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

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
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    @ConditionalOnBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
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
