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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

/**
 * Base spring application run listener.
 */
public class BaseSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {

    private final SpringApplication application;

    private final String[] args;

    private List<Hippo4jApplicationListener> hippo4jApplicationListeners = new ArrayList();

    {
        hippo4jApplicationListeners.add(new StartingApplicationListener());
    }

    public BaseSpringApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {
        hippo4jApplicationListeners.forEach(each -> each.starting());
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        hippo4jApplicationListeners.forEach(each -> each.contextPrepared(context));
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        hippo4jApplicationListeners.forEach(each -> each.started(context));
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        hippo4jApplicationListeners.forEach(each -> each.failed(context, exception));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
