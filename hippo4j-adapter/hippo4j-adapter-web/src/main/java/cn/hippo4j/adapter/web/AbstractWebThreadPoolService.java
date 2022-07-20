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

import cn.hippo4j.common.config.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;

/**
 * Abstract web thread pool service.
 */
@Slf4j
public abstract class AbstractWebThreadPoolService implements WebThreadPoolService, ApplicationRunner {

    /**
     * Thread pool executor.
     */
    protected volatile Executor executor;

    /**
     * Get web thread pool by server.
     *
     * @param webServer
     * @return
     */
    protected abstract Executor getWebThreadPoolByServer(WebServer webServer);

    @Override
    public Executor getWebThreadPool() {
        if (executor == null) {
            synchronized (AbstractWebThreadPoolService.class) {
                if (executor == null) {
                    ApplicationContext applicationContext = ApplicationContextHolder.getInstance();
                    WebServer webServer = ((WebServerApplicationContext) applicationContext).getWebServer();
                    executor = getWebThreadPoolByServer(webServer);
                }
            }
        }
        return executor;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            getWebThreadPool();
        } catch (Exception ex) {
            // ignore. Adaptation unit test.
        }
    }
}
