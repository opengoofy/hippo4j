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

package cn.hippo4j.springboot3.starter.adapter.web;

import cn.hippo4j.adapter.web.WebThreadPoolRunStateHandler;
import cn.hippo4j.adapter.web.jetty.DefaultJettyWebThreadPoolHandler;
import cn.hippo4j.adapter.web.jetty.JettyWebThreadPoolHandlerAdapt;
import cn.hippo4j.adapter.web.tomcat.DefaultTomcatWebThreadPoolHandler;
import cn.hippo4j.adapter.web.tomcat.TomcatWebThreadPoolHandlerAdapt;
import cn.hippo4j.adapter.web.undertow.DefaultUndertowWebThreadPoolHandler;
import cn.hippo4j.adapter.web.undertow.UndertowWebThreadPoolHandlerAdapt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web Thread Pool Handler Configuration
 **/
@Configuration(proxyBeanMethods = false)
public class WebThreadPoolHandlerConfiguration {

    /**
     * embedded tomcat
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat", "org.apache.coyote.UpgradeProtocol", "jakarta.servlet.Servlet"})
    @ConditionalOnBean(value = ConfigurableTomcatWebServerFactory.class, search = SearchStrategy.CURRENT)
    @ConditionalOnMissingBean({DefaultTomcatWebThreadPoolHandler.class, TomcatWebThreadPoolHandlerAdapt.class})
    static class EmbeddedTomcat {

        @Bean
        public TomcatWebThreadPoolHandlerAdapt tomcatWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new DefaultTomcatWebThreadPoolHandler(webThreadPoolRunStateHandler);
        }
    }

    /**
     * embedded jetty
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = {"jakarta.servlet.Servlet", "org.eclipse.jetty.server.Server", "org.eclipse.jetty.util.Loader", "org.eclipse.jetty.ee10.webapp.WebAppContext"})
    @ConditionalOnBean(value = ConfigurableJettyWebServerFactory.class, search = SearchStrategy.CURRENT)
    @ConditionalOnMissingBean({DefaultJettyWebThreadPoolHandler.class, JettyWebThreadPoolHandlerAdapt.class})
    static class EmbeddedJetty {

        @Bean
        public JettyWebThreadPoolHandlerAdapt jettyWebThreadPoolHandler() {
            return new DefaultJettyWebThreadPoolHandler();
        }
    }

    /**
     * embedded undertow
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = {"jakarta.servlet.Servlet", "org.xnio.SslClientAuthMode", "io.undertow.Undertow"})
    @ConditionalOnBean(value = ConfigurableUndertowWebServerFactory.class, search = SearchStrategy.CURRENT)
    @ConditionalOnMissingBean({DefaultUndertowWebThreadPoolHandler.class, UndertowWebThreadPoolHandlerAdapt.class})
    static class EmbeddedUndertow {

        @Bean
        public UndertowWebThreadPoolHandlerAdapt undertowWebThreadPoolHandler(WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
            return new DefaultUndertowWebThreadPoolHandler(webThreadPoolRunStateHandler);
        }
    }
}
