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

import cn.hippo4j.common.model.ThreadDetailStateInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;

import java.util.List;

/**
 * Web thread-pool run state api.
 */
public interface WebThreadPoolRunStateApi {

    /**
     * Get the run state info of the web thread pool
     *
     * @param threadPoolId the thread pool id
     * @return the info
     */
    Result<ThreadPoolRunStateInfo> getPoolRunState(String threadPoolId);

    /**
     * Get the run state detail of the web thread pool
     *
     * @param threadPoolId the thread pool id
     * @return the detail
     */
    Result<List<ThreadDetailStateInfo>> getThreadStateDetail(String threadPoolId);
}
