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

package cn.hippo4j.config.springboot1x.starter.web;

import cn.hippo4j.adapter.web.AbstractWebThreadPoolService;
import cn.hippo4j.adapter.web.IWebThreadPoolHandlerSupport;
import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;

/**
 * Abstract class for adapting WebThreadPoolService to Spring 1.x version.
 */
@Slf4j
public abstract class AbstractWebThreadPoolService1x extends AbstractWebThreadPoolService {

    public AbstractWebThreadPoolService1x(IWebThreadPoolHandlerSupport support) {
        super(support);
    }

    private static final String STARTED_FIELD_NAME = "started";

    /**
     * Get the embedded Servlet container from the Spring application context.
     */
    protected EmbeddedServletContainer getContainer() {
        return ((EmbeddedWebApplicationContext) ApplicationContextHolder.getInstance()).getEmbeddedServletContainer();
    }

    /**
     * Get the port from web container.
     */
    @Override
    public Integer getPort() {
        return getContainer().getPort();
    }

}
