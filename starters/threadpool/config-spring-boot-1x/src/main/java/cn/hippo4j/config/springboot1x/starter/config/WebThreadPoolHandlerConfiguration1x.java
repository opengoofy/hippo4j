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

package cn.hippo4j.config.springboot1x.starter.config;

import cn.hippo4j.adapter.web.WebThreadPoolRunStateHandler;
import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.config.springboot1x.starter.web.jetty.JettyWebThreadPoolHandler1x;
import cn.hippo4j.config.springboot1x.starter.web.tomcat.TomcatWebThreadPoolHandler1x;
import cn.hippo4j.config.springboot1x.starter.web.undertow.UndertowWebThreadPoolHandler1x;
import cn.hippo4j.springboot.starter.adapter.web.WebThreadPoolHandlerConfiguration;
import io.undertow.Undertow;
import org.apache.catalina.Loader;
import org.apache.catalina.Server;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

import javax.servlet.Servlet;

/**
 * Spring auto-configuration class for WebThreadPoolHandlers.
 */
@Configuration
@AutoConfigureBefore(WebThreadPoolHandlerConfiguration.class)
public class WebThreadPoolHandlerConfiguration1x {

    /**
     * Embedded tomcat
     */
    @Configuration
    @ConditionalOnClass({Servlet.class, Tomcat.class})
    @ConditionalOnBean(WebThreadPoolRunStateHandler.class)
    @ConditionalOnMissingBean(value = WebThreadPoolService.class, search = SearchStrategy.CURRENT)
    static class EmbeddedTomcat {

        /**
         * Nested configuration if Tomcat is being used.
         */
        @Bean
        public WebThreadPoolService tomcatWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new TomcatWebThreadPoolHandler1x(webThreadPoolRunStateHandler);
        }
    }

    /**
     * Nested configuration if Jetty is being used.
     */
    @Configuration
    @ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
    @ConditionalOnBean(WebThreadPoolRunStateHandler.class)
    @ConditionalOnMissingBean(value = WebThreadPoolService.class, search = SearchStrategy.CURRENT)
    static class EmbeddedJetty {

        @Bean
        public WebThreadPoolService jettyWebThreadPoolHandler() {
            return new JettyWebThreadPoolHandler1x();
        }
    }

    /**
     * Nested configuration if Undertow is being used.
     */
    @Configuration
    @ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
    @ConditionalOnBean(WebThreadPoolRunStateHandler.class)
    @ConditionalOnMissingBean(value = WebThreadPoolService.class, search = SearchStrategy.CURRENT)
    static class EmbeddedUndertow {

        @Bean
        public WebThreadPoolService undertowWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new UndertowWebThreadPoolHandler1x(webThreadPoolRunStateHandler);
        }
    }
}
