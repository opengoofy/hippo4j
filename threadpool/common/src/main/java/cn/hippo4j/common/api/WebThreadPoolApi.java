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

package cn.hippo4j.common.api;

import cn.hippo4j.common.model.ThreadPoolBaseInfo;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;

/**
 * Web thread pool api.
 */
public interface WebThreadPoolApi {

    /**
     * Get thread pool information by identifying the mark
     *
     * @param mark Third party frame identification
     * @return thread pool info
     */
    Result<ThreadPoolBaseInfo> getPoolBaseState(String mark);

    /**
     * Get all thread pool information
     *
     * @return thread pool info
     */
    Result<ThreadPoolRunStateInfo> getPoolRunState();

    /**
     * Example Modify the thread pool information
     *
     * @param threadPoolParameterInfo update info
     */
    Result<Void> updateWebThreadPool(ThreadPoolParameterInfo threadPoolParameterInfo);

}
