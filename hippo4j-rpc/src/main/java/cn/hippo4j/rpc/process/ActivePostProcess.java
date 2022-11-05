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

package cn.hippo4j.rpc.process;

import cn.hippo4j.rpc.request.Request;
import cn.hippo4j.rpc.response.Response;

/**
 * Callback while the connection is in progress
 */
public interface ActivePostProcess {

    /**
     * Client: After establishing a connection and before passing parameters<br>
     * Server: Receives parameters and performs pre-call operations<br>
     *
     * @param request request
     * @return Whether to continue the execution. If it is a client, the returned value does not affect subsequent execution
     */
    default boolean preHandler(Request request) {
        return true;
    }

    /**
     * Client: Action after receiving a response<br>
     * Server: performs the operation after the call<br>
     *
     * @param request  request
     * @param response response
     */
    default void postHandler(Request request, Response response) {
        // NO SOMETHING
    }

    /**
     * Called when an exception or resource is cleaned
     *
     * @param request  request
     * @param response response
     * @param e        Exception
     */
    default void afterCompletion(Request request, Response response, Exception e) {
        // NO SOMETHING
    }
}
