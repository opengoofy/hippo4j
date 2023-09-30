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

import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;

import java.io.Closeable;

/**
 * <h3>CLIENT</h3>
 * The highest level interface for the client, it does not care how to communicate with the server,
 * nor can it know the specific connection information. The client plays the role of message producer
 * in the whole connection.By sending a Request to the server ({@link Request}), the client will be
 * able to communicate with the server. Wait for the server's Response ({@link Response})
 * <h3>METHOD</h3>
 * <ul>
 *     <li>{@link #connect(Request)}</li>
 *     <li>{@link #close()}</li>
 * </ul>
 * You can usually use the client in this way:
 * <pre>
 *     Request request = new Request();
 *     try(Client client = new Client()){
 *         Response response = client.connection(request);
 *     }
 * </pre>
 *
 * <b>The client implements Closeable and supports automatic shutdown, However, you can manually
 * disable it when you want to use it</b>
 *
 * @since 2.0.0
 */
public interface Client extends Closeable {

    /**
     * Start the client and try to send and receive data
     *
     * @param request Request information, Requested methods and parameters
     * @return response Response from server side
     */
    <R> R connect(Request request);

}
