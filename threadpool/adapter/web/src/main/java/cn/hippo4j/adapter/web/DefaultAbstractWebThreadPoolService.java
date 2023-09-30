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

package cn.hippo4j.adapter.web;

import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;

/**
 * Default WebThreadPoolService abstract class,
 * reuses common capabilities for web container operations.
 */
@Slf4j
public abstract class DefaultAbstractWebThreadPoolService extends AbstractWebThreadPoolService {

    public DefaultAbstractWebThreadPoolService(IWebThreadPoolHandlerSupport support) {
        super(support);
    }

    private static final String STARTED_FIELD_NAME = "started";

    /**
     * Get the internal abstract method of the web container thread pool,
     * to be implemented by subclasses.
     * @return
     */
    @Override
    protected Executor getWebThreadPoolInternal() {
        return getWebThreadPoolByServer(getWebServer());
    }

    /**
     * Get port by server.
     * @return web port
     */
    @Override
    public Integer getPort() {
        return getWebServer().getPort();
    }

    /**
     * Get the thread pool object of the current web container based on the WebServer.
     * @param webServer current Web-Server.
     * @return Thread pool executor of the current web container.
     */
    protected abstract Executor getWebThreadPoolByServer(WebServer webServer);

    /**
     * Get current Web Server.
     * @return webServer current Web-Server.
     */
    public WebServer getWebServer() {
        ApplicationContext applicationContext = ApplicationContextHolder.getInstance();
        return ((WebServerApplicationContext) applicationContext).getWebServer();
    }

}
