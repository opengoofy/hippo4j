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

package cn.hippo4j.springboot.starter.adapter.hystrix;

import cn.hippo4j.adapter.hystrix.HystrixThreadPoolAdapter4Config;
import cn.hippo4j.adapter.hystrix.HystrixThreadPoolAdapter4Server;
import cn.hippo4j.adapter.hystrix.ThreadPoolAdapterScheduler;
import cn.hippo4j.core.config.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: hippo4j
 * @description:
 * @author: lk
 * @create: 2022-07-15
 **/
@Configuration(proxyBeanMethods = false)
public class HystrixAdapterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public ThreadPoolAdapterScheduler threadPoolAdapterScheduler() {
        return new ThreadPoolAdapterScheduler();
    }

    @Bean
    @ConditionalOnClass(name = "cn.hippo4j.springboot.starter.config.DynamicThreadPoolAutoConfiguration")
    public HystrixThreadPoolAdapter4Server hystrixThreadPoolAdapter4Server(ThreadPoolAdapterScheduler threadPoolAdapterScheduler) {
        return new HystrixThreadPoolAdapter4Server(threadPoolAdapterScheduler);
    }

    @Bean
    @ConditionalOnClass(name = "cn.hippo4j.config.springboot.starter.config.DynamicThreadPoolAutoConfiguration")
    public HystrixThreadPoolAdapter4Config hystrixThreadPoolAdapter4Config(ThreadPoolAdapterScheduler threadPoolAdapterScheduler) {
        return new HystrixThreadPoolAdapter4Config(threadPoolAdapterScheduler);
    }
}
