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

package cn.hippo4j.rpc.connection;

import cn.hippo4j.rpc.model.Request;

import java.io.Closeable;

/**
 * Applicable to client connections<br>
 * Represents a network request connection and provides IO layer support<br>
 * <p>
 * This is not a strict and stateless Connection interface, it contains the necessary
 * operations that should be done in the connection. It is more like integrating the
 * connection and the connection channel together, so creating {@link ClientConnection} is
 * very resource intensive, for which caching is recommended
 *
 * @since 2.0.0
 */
public interface ClientConnection extends Closeable {

    /**
     * Establish a connection and process
     *
     * @param request Request information
     */
    <R> R connect(Request request);

    /**
     * Get timeout, ms
     */
    long timeout();

    /**
     * SET timeout, ms
     */
    void setTimeout(long timeout);
}
