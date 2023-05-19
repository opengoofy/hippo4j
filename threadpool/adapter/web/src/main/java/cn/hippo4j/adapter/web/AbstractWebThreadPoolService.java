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

import cn.hippo4j.common.extension.enums.WebContainerEnum;
import cn.hippo4j.common.model.ThreadPoolBaseInfo;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.concurrent.Executor;

/**
 * Abstract web thread pool service.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public abstract class AbstractWebThreadPoolService implements WebThreadPoolService, ApplicationRunner {

    private final IWebThreadPoolHandlerSupport support;

    public AbstractWebThreadPoolService(IWebThreadPoolHandlerSupport support) {
        this.support = support;
    }

    /**
     * Thread pool executor
     */
    protected volatile Executor executor;

    /**
     * Get web thread pool by server
     *
     * @return
     */
    protected abstract Executor getWebThreadPoolInternal();

    @Override
    public Executor getWebThreadPool() {
        if (executor == null) {
            synchronized (AbstractWebThreadPoolService.class) {
                if (executor == null) {
                    executor = getWebThreadPoolInternal();
                }
            }
        }
        return executor;
    }

    @Override
    public ThreadPoolBaseInfo simpleInfo() {
        return support.simpleInfo();
    }

    @Override
    public ThreadPoolParameter getWebThreadPoolParameter() {
        return support.getWebThreadPoolParameter();
    }

    @Override
    public ThreadPoolRunStateInfo getWebRunStateInfo() {
        return support.getWebRunStateInfo();
    }

    @Override
    public void updateWebThreadPool(ThreadPoolParameterInfo threadPoolParameterInfo) {
        support.updateWebThreadPool(threadPoolParameterInfo);
    }

    @Override
    public WebContainerEnum getWebContainerType() {
        return support.getWebContainerType();
    }

    /**
     * Call-back after the web container has been started.
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            this.support.setExecutor(getWebThreadPool());
        } catch (Exception ignored) {
        }
    }
}
