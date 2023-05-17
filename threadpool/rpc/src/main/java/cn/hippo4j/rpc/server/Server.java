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

package cn.hippo4j.rpc.server;

import java.io.Closeable;

/**
 * the service for RPC, Explain the role of the service in the request
 *
 * @since 2.0.0
 */
public interface Server extends Closeable {

    /**
     * Start the server. Attempt to listen on the port and receive the request.<br>
     * If the port being processed is already bound, an exception is thrown
     */
    void bind();

    /**
     * Check whether the server is active
     *
     * @return Whether active
     */
    boolean isActive();

}
