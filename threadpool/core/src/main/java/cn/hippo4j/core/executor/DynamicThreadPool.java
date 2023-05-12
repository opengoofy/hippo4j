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

package cn.hippo4j.core.executor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that enhances the functionality of the jdk acoustic thread pool,
 * with the following list of enhancements.
 * <ul>
 *      <li>Dynamic change at runtime</li>
 *      <li>Determine whether an alarm is required at runtime</li>
 *      <li>Provide observable monitoring indicators</li>
 *      <li>......</li>
 * </ur>
 *
 * <p>If you use Server mode, you can view the thread pool operation in the built-in console.
 * <p>If you use Config mode, you can observe with Prometheus and Grafana.
 *
 * <p>The annotation is normally marked on the
 * spring bean defined by {@link java.util.concurrent.ThreadPoolExecutor}.
 *
 * <p>Can also be marked on the following types:
 *
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 * @see com.alibaba.ttl.threadpool.ExecutorTtlWrapper
 * @see com.alibaba.ttl.threadpool.ExecutorServiceTtlWrapper
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicThreadPool {
}
