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

package cn.hippo4j.rpc.client;

import cn.hippo4j.rpc.request.Request;
import cn.hippo4j.rpc.response.Response;

import java.io.Closeable;

/**
 * the client for RPC, Explain the role of the client in the request
 */
public interface Client extends Closeable {

    /**
     * Start the client and try to send and receive data
     */
    Response connection(Request request);

    /**
     * Check whether the client is active
     *
     * @return Whether active
     */
    boolean isActive();

}
