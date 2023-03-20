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

package cn.hippo4j.config.springboot1x.starter.web.jetty;

import cn.hippo4j.adapter.web.jetty.JettyWebThreadPoolHandlerSupport;
import cn.hippo4j.config.springboot1x.starter.web.AbstractWebThreadPoolService1x;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer;

import java.util.concurrent.Executor;

/**
 * WebThreadPoolHandler compatible with Jetty container for Spring 1.x version.
 */
@Slf4j
public class JettyWebThreadPoolHandler1x extends AbstractWebThreadPoolService1x {

    public JettyWebThreadPoolHandler1x() {
        super(new JettyWebThreadPoolHandlerSupport());
    }

    @Override
    protected Executor getWebThreadPoolInternal() {
        try {
            return ((JettyEmbeddedServletContainer) getContainer()).getServer().getThreadPool();
        } catch (Throwable th) {
            log.error("Failed to get Jetty thread pool.", th);
            return null;
        }
    }

}
