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

package cn.hippo4j.rpc.handler;

import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;

/**
 * The handler in each connection, where the specific behavior of the connection
 * must be specified, such as serialization and parsing, requesting and receiving
 * requests, and so on<br>
 *
 * @since 2.0.0
 */
public interface ConnectHandler {

    /**
     * Processing after receiving the request
     *
     * @param request request
     */
    default Response sendHandler(Request request) {
        return null;
    }

    /**
     * Processing after receiving Response<br>
     * This is mainly for subsequent processing of the results
     *
     * @param response response
     */
    default void handler(Response response) {
        //
    }

}
