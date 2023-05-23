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

package cn.hippo4j.threadpool.dynamic.mode.config.properties;

import lombok.Data;

/**
 * Notify platform properties.
 */
@Data
public class NotifyPlatformProperties {

    /**
     * Platform
     */
    private String platform;

    /**
     * Secret key. {@link NotifyPlatformProperties#token}
     */
    @Deprecated
    private String secretKey;

    /**
     * Token
     */
    private String token;

    /**
     * Secret
     */
    private String secret;
}
