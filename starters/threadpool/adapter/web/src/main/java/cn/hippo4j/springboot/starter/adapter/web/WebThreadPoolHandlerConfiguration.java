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

import cn.hippo4j.adapter.web.jetty.DefaultJettyWebThreadPoolHandler;
import cn.hippo4j.adapter.web.jetty.JettyWebThreadPoolHandlerAdapt;
import cn.hippo4j.adapter.web.tomcat.DefaultTomcatWebThreadPoolHandler;
import cn.hippo4j.adapter.web.tomcat.TomcatWebThreadPoolHandlerAdapt;
import cn.hippo4j.adapter.web.undertow.DefaultUndertowWebThreadPoolHandler;
import cn.hippo4j.adapter.web.WebThreadPoolRunStateHandler;
import cn.hippo4j.adapter.web.undertow.UndertowWebThreadPoolHandlerAdapt;
import io.undertow.Undertow;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

import javax.servlet.Servlet;

/**
 * Web Thread Pool Handler Configuration
 **/
@Configuration(proxyBeanMethods = false)
public class WebThreadPoolHandlerConfiguration {

    /**
     * embedded tomcat
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    @ConditionalOnBean(value = ConfigurableTomcatWebServerFactory.class, search = SearchStrategy.CURRENT)
    @ConditionalOnMissingBean({DefaultTomcatWebThreadPoolHandler.class, TomcatWebThreadPoolHandlerAdapt.class})
    static class EmbeddedTomcat {

        /**
         * Refer to the Tomcat loading source code .
         * This load is performed if the {@link Tomcat} class exists and
         * the Web embedded server loads the {@link ServletWebServerFactory} top-level interface type at the same time
         */
        @Bean
        public TomcatWebThreadPoolHandlerAdapt tomcatWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new DefaultTomcatWebThreadPoolHandler(webThreadPoolRunStateHandler);
        }
    }

    /**
     * embedded jetty
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
    @ConditionalOnBean(value = ConfigurableJettyWebServerFactory.class, search = SearchStrategy.CURRENT)
    @ConditionalOnMissingBean({DefaultJettyWebThreadPoolHandler.class, JettyWebThreadPoolHandlerAdapt.class})
    static class EmbeddedJetty {

        /**
         * Refer to the Server loading source code .
         * This load is performed if the {@link Server} class exists and
         * the Web embedded server loads the {@link ServletWebServerFactory} top-level interface type at the same time
         */
        @Bean
        public JettyWebThreadPoolHandlerAdapt jettyWebThreadPoolHandler() {
            return new DefaultJettyWebThreadPoolHandler();
        }
    }

    /**
     * embedded undertow
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
    @ConditionalOnBean(value = ConfigurableUndertowWebServerFactory.class, search = SearchStrategy.CURRENT)
    @ConditionalOnMissingBean({DefaultUndertowWebThreadPoolHandler.class, UndertowWebThreadPoolHandlerAdapt.class})
    static class EmbeddedUndertow {

        /**
         * Refer to the Undertow loading source code .
         * This load is performed if the {@link Undertow} class exists and
         * the Web embedded server loads the {@link ServletWebServerFactory} top-level interface type at the same time
         */
        @Bean
        public UndertowWebThreadPoolHandlerAdapt undertowWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new DefaultUndertowWebThreadPoolHandler(webThreadPoolRunStateHandler);
        }
    }
}
