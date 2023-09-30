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

package cn.hippo4j.springboot.starter.remote;

import cn.hippo4j.common.model.Result;

import java.util.Map;

/**
 * Http agent.
 */
public interface HttpAgent {

    /**
     * Start.
     */
    void start();

    /**
     * Get tenant id.
     *
     * @return
     */
    String getTenantId();

    /**
     * Get encode.
     *
     * @return
     */
    String getEncode();

    /**
     * Http get simple.
     *
     * @param path
     * @return
     */
    Result httpGetSimple(String path);

    /**
     * Http post.
     *
     * @param path
     * @param body
     * @return
     */
    Result httpPost(String path, Object body);

    /**
     * Send HTTP post request by discovery.
     *
     * @param path
     * @param body
     * @return
     */
    Result httpPostByDiscovery(String path, Object body);

    /**
     * Send HTTP get request by dynamic config.
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpGetByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                           long readTimeoutMs);

    /**
     * Send HTTP post request by dynamic config.
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpPostByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                            long readTimeoutMs);

    /**
     * Send HTTP delete request by dynamic config.
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpDeleteByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                              long readTimeoutMs);
}
