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

package cn.hippo4j.starter.test;

import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.common.toolkit.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Rejected execution handler proxy test.
 *
 * @author chen.ma
 * @date 2022/2/17 19:52
 */
@Slf4j
public class RejectedExecutionHandlerProxyTest {

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            test(i + "");
        }
    }

    private static void test(String threadPoolId) {
        ThreadPoolExecutor executor = ThreadPoolBuilder.builder()
                .threadPoolId(threadPoolId)
                .threadFactory(threadPoolId)
                .poolThreadSize(1, 1)
                .workQueue(new LinkedBlockingQueue(1))
                .dynamicPool()
                .build();

        for (int i = 0; i < 300; i++) {
            try {
                executor.execute(() -> ThreadUtil.sleep(Integer.MAX_VALUE));
            } catch (Exception ex) {
                log.error("ThreadPool name :: {}, Exception :: ", Thread.currentThread().getName(), ex);
            }
        }

        ThreadUtil.sleep(1000);

        DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) executor;
        long rejectCount = dynamicThreadPoolExecutor.getRejectCountNum();
        log.info("ThreadPool name :: {}, Reject count :: {}", Thread.currentThread().getName(), rejectCount);
    }
}
