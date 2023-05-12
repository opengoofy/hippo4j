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

package cn.hippo4j.server.listener;

import cn.hippo4j.config.toolkit.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Paths;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Starting application listener.
 */
@Slf4j
public class StartingApplicationListener implements Hippo4jApplicationListener {

    private volatile boolean starting;

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void starting() {
        starting = true;
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (EnvUtil.getStandaloneMode()) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(
                    1,
                    r -> {
                        Thread thread = new Thread(r);
                        thread.setName("server.hippo4j-starting");
                        return thread;
                    });
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                if (starting) {
                    log.info("Hippo4j is starting...");
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        starting = false;
        closeExecutor();
        if (EnvUtil.getStandaloneMode()) {
            log.info("Hippo4j started successfully...");
        }
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.error("Startup errors: {}", exception);
        closeExecutor();
        context.close();
        log.error("Hippo4j failed to start, please see {} for more details.",
                Paths.get(EnvUtil.getHippo4jHome(), "logs/hippo4j.log"));
    }

    private void closeExecutor() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
    }
}
