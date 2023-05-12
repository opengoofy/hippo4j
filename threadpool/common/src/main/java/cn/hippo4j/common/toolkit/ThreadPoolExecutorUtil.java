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

package cn.hippo4j.common.toolkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pool util
 *
 * @author yangjie
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolExecutorUtil {

    /**
     * Set the thread pool size in a safe way.
     * <p>
     * see https://github.com/opengoofy/hippo4j/issues/1072
     */
    public static void safeSetPoolSize(ThreadPoolExecutor executor, int newCorePoolSize, int newMaximumPoolSize) {
        Assert.isTrue(newCorePoolSize <= newMaximumPoolSize, "newCorePoolSize must be smaller than newMaximumPoolSize");
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        if (newCorePoolSize > originalMaximumPoolSize) {
            executor.setMaximumPoolSize(newMaximumPoolSize);
            executor.setCorePoolSize(newCorePoolSize);
        } else {
            executor.setCorePoolSize(newCorePoolSize);
            executor.setMaximumPoolSize(newMaximumPoolSize);
        }
    }
}
