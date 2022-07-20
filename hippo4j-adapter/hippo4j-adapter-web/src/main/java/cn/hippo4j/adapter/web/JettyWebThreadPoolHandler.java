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

import cn.hippo4j.common.constant.ChangeThreadPoolConstants;
import cn.hippo4j.common.model.ThreadPoolBaseInfo;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * Jetty web thread pool handler.
 */
@Slf4j
public class JettyWebThreadPoolHandler extends AbstractWebThreadPoolService {

    @Override
    protected Executor getWebThreadPoolByServer(WebServer webServer) {
        JettyWebServer jettyWebServer = (JettyWebServer) webServer;
        return jettyWebServer.getServer().getThreadPool();
    }

    @Override
    public ThreadPoolBaseInfo simpleInfo() {
        ThreadPoolBaseInfo poolBaseInfo = new ThreadPoolBaseInfo();
        QueuedThreadPool queuedThreadPool = (QueuedThreadPool) executor;
        poolBaseInfo.setCoreSize(queuedThreadPool.getMinThreads());
        poolBaseInfo.setMaximumSize(queuedThreadPool.getMaxThreads());
        BlockingQueue jobs = (BlockingQueue) ReflectUtil.getFieldValue(queuedThreadPool, "_jobs");
        int queueCapacity = jobs.remainingCapacity() + jobs.size();
        poolBaseInfo.setQueueCapacity(queueCapacity);
        poolBaseInfo.setQueueType(jobs.getClass().getSimpleName());
        poolBaseInfo.setKeepAliveTime((long) queuedThreadPool.getIdleTimeout());
        poolBaseInfo.setRejectedName("RejectedExecutionException");
        return poolBaseInfo;
    }

    @Override
    public ThreadPoolParameter getWebThreadPoolParameter() {
        ThreadPoolParameterInfo parameterInfo = null;
        try {
            parameterInfo = new ThreadPoolParameterInfo();
            QueuedThreadPool jettyExecutor = (QueuedThreadPool) executor;
            int minThreads = jettyExecutor.getMinThreads();
            int maxThreads = jettyExecutor.getMaxThreads();
            parameterInfo.setCoreSize(minThreads);
            parameterInfo.setMaxSize(maxThreads);
        } catch (Exception ex) {
            log.error("Failed to get the jetty thread pool parameter.", ex);
        }
        return parameterInfo;
    }

    @Override
    public ThreadPoolRunStateInfo getWebRunStateInfo() {
        return null;
    }

    @Override
    public void updateWebThreadPool(ThreadPoolParameterInfo threadPoolParameterInfo) {
        try {
            QueuedThreadPool jettyExecutor = (QueuedThreadPool) executor;
            int minThreads = jettyExecutor.getMinThreads();
            int maxThreads = jettyExecutor.getMaxThreads();
            Integer coreSize = threadPoolParameterInfo.corePoolSizeAdapt();
            Integer maxSize = threadPoolParameterInfo.maximumPoolSizeAdapt();
            jettyExecutor.setMinThreads(coreSize);
            jettyExecutor.setMaxThreads(maxSize);
            log.info("[JETTY] Changed web thread pool. corePoolSize :: [{}], maximumPoolSize :: [{}]",
                    String.format(ChangeThreadPoolConstants.CHANGE_DELIMITER, minThreads, jettyExecutor.getMinThreads()),
                    String.format(ChangeThreadPoolConstants.CHANGE_DELIMITER, maxThreads, jettyExecutor.getMaxThreads()));
        } catch (Exception ex) {
            log.error("Failed to modify the jetty thread pool parameter.", ex);
        }
    }
}
