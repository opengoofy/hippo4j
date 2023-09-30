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

package cn.hippo4j.config.springboot1x.starter.web.undertow;

import cn.hippo4j.adapter.web.undertow.UndertowWebThreadPoolHandlerSupport;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import cn.hippo4j.config.springboot1x.starter.web.AbstractWebThreadPoolService1x;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

/**
 * WebThreadPoolHandler compatible with Undertow container for Spring 1.x version.
 */
@Slf4j
public class UndertowWebThreadPoolHandler1x extends AbstractWebThreadPoolService1x {

    private static final String UNDERTOW_NAME = "undertow";

    public UndertowWebThreadPoolHandler1x(AbstractThreadPoolRuntime runtime) {
        super(new UndertowWebThreadPoolHandlerSupport(runtime));
    }

    @Override
    protected Executor getWebThreadPoolInternal() {
        try {
            UndertowEmbeddedServletContainer container = (UndertowEmbeddedServletContainer) getContainer();
            Field field = ReflectionUtils.findField(UndertowEmbeddedServletContainer.class, UNDERTOW_NAME);
            ReflectionUtils.makeAccessible(field);
            Undertow undertow = (Undertow) ReflectionUtils.getField(field, container);
            return undertow.getWorker();
        } catch (Throwable th) {
            log.error("Failed to get Undertow thread pool.", th);
            return null;
        }
    }

}
