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

package cn.hippo4j.rpc.exception;

/**
 * If there is a timeout between the server and the client, you will get a {@link TimeOutException},
 * which is not different from {@link RuntimeException}, but it will be more explicit about the type of exception, right
 *
 * @since 2.0.0
 */
public class TimeOutException extends RuntimeException {

    private static final long serialVersionUID = 8247610319171014183L;

    public TimeOutException() {
        super();
    }

    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(Throwable e) {
        super(e.getMessage(), e);
    }

    public TimeOutException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TimeOutException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

}
