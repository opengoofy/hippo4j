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

package cn.hippo4j.adapter.web.undertow;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.Executor;

import cn.hippo4j.adapter.web.DefaultAbstractWebThreadPoolService;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.util.ReflectionUtils;

/**
 * Undertow web thread pool handler.
 */
@Slf4j
public class DefaultUndertowWebThreadPoolHandler extends DefaultAbstractWebThreadPoolService
        implements
            UndertowWebThreadPoolHandlerAdapt {

    private static final String UNDERTOW_NAME = "undertow";

    public DefaultUndertowWebThreadPoolHandler(AbstractThreadPoolRuntime runtime) {
        super(new UndertowWebThreadPoolHandlerSupport(runtime));
    }

    /**
     * Get the thread pool object of the current web container based on the WebServer.
     * @param webServer current Web-Server.
     * @return Thread pool executor of the current web container.
     */
    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        // There is no need to consider reflection performance because the fetch is a singleton.
        // Springboot 2-3 version, can directly through reflection to obtain the undertow property
        UndertowServletWebServer undertowServletWebServer = (UndertowServletWebServer) webServer;
        Field undertowField = ReflectionUtils.findField(UndertowServletWebServer.class, UNDERTOW_NAME);
        ReflectionUtils.makeAccessible(undertowField);

        Undertow undertow = (Undertow) ReflectionUtils.getField(undertowField, undertowServletWebServer);
        return Objects.isNull(undertow) ? null : undertow.getWorker();
    }

}
