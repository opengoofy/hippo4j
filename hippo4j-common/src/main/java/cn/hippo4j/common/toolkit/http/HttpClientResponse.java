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

package cn.hippo4j.common.toolkit.http;

import java.io.Closeable;
import java.io.InputStream;

/**
 * Represents a client-side HTTP response.
 *
 * @author mai.jh
 */
public interface HttpClientResponse extends Closeable {

    /**
     * Return the headers of this message.
     *
     * @return a corresponding HttpHeaders object (never {@code null})
     */
    Header getHeaders();

    /**
     * Return the body of the message as an input stream.
     *
     * @return String response body
     */
    InputStream getBody();

    /**
     * Return the HTTP status code.
     *
     * @return the HTTP status as an integer
     */
    int getStatusCode();

    /**
     * Return the HTTP status text of the response.
     *
     * @return the HTTP status text
     */
    String getStatusText();

    /**
     * Return the body As string.
     *
     * @return
     */
    String getBodyString();

    /**
     * close response InputStream.
     */
    @Override
    void close();
}
