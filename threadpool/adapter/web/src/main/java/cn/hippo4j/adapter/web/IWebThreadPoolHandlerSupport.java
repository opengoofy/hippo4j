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

import java.util.concurrent.Executor;

/**
 * Support class for WebThreadPoolHandler, providing some common methods.
 */
public interface IWebThreadPoolHandlerSupport {

    /**
     * Set the Executor to the current class
     * so that other methods in the class can function properly.
     * @param executor
     */
    void setExecutor(Executor executor);

    /**
     * Retrieve the simple information of the thread pool.
     * @return
     */
    ThreadPoolBaseInfo simpleInfo();

    /**
     * Retrieve the parameter of the thread pool.
     * @return
     */
    ThreadPoolParameter getWebThreadPoolParameter();

    /**
     * Retrieve the run state of the thread pool.
     * @return
     */
    ThreadPoolRunStateInfo getWebRunStateInfo();

    /**
     * Update thread pool parameters.
     * @param threadPoolParameterInfo New parameters
     */
    void updateWebThreadPool(ThreadPoolParameterInfo threadPoolParameterInfo);

    WebContainerEnum getWebContainerType();
}
