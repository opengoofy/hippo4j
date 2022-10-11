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

package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.common.toolkit.HttpClientUtil;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import cn.hippo4j.springboot.starter.remote.ServerHttpAgent;
import org.springframework.context.annotation.Bean;

/**
 * Http client config.
 */
public class HttpClientConfiguration {

    @Bean
    public HttpClientUtil hippo4JHttpClientUtil() {
        return HttpClientUtil.build();
    }

    @Bean
    @SuppressWarnings("all")
    public HttpAgent httpAgent(BootstrapProperties properties, HttpClientUtil hippo4JHttpClientUtil) {
        return new ServerHttpAgent(properties, hippo4JHttpClientUtil);
    }
}
