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

import java.io.Closeable;

/**
 * Represents a network request connection and provides IO layer support
 */
public interface Connection extends Closeable {

    /**
     * Gets the state of the connection, which is interpreted differently by different programs<br>
     * <p>
     * Client: Active connection indicates that a connection is being maintained with the server.
     * Inactive connection indicates that no connection is being established with the server<br>
     * <p>
     * Server: The active connection indicates that the server has been started, is receiving ports,
     * and can obtain requests at any time. The inactive connection indicates that the server has been
     * shut down and the ports have been released
     *
     * @return Whether active
     */
    boolean isActive();

}
