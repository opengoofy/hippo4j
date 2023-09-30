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

package cn.hippo4j.adapter.web.tomcat;

import cn.hippo4j.adapter.web.DefaultAbstractWebThreadPoolService;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tomcat web thread pool handler.
 */
@Slf4j
public class DefaultTomcatWebThreadPoolHandler extends DefaultAbstractWebThreadPoolService
        implements
            TomcatWebThreadPoolHandlerAdapt {

    private final AtomicBoolean cacheFlag = new AtomicBoolean(Boolean.FALSE);

    private static String exceptionMessage;

    public DefaultTomcatWebThreadPoolHandler(AbstractThreadPoolRuntime runtime) {
        super(new TomcatWebThreadPoolHandlerSupport(runtime));
    }

    /**
     * Get the thread pool object of the current web container based on the WebServer.
     * @param webServer current Web-Server.
     * @return Thread pool executor of the current web container.
     */
    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        if (cacheFlag.get()) {
            log.warn("Exception getting Tomcat thread pool. Exception message: {}", exceptionMessage);
            return null;
        }
        Executor tomcatExecutor = null;
        try {
            tomcatExecutor = ((TomcatWebServer) webServer).getTomcat().getConnector().getProtocolHandler().getExecutor();
        } catch (Exception ex) {
            cacheFlag.set(Boolean.TRUE);
            exceptionMessage = ex.getMessage();
            log.error("Failed to get Tomcat thread pool. Message: {}", exceptionMessage);
        }
        return tomcatExecutor;
    }
}
