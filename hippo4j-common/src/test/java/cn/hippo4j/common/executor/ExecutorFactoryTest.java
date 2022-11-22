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

package cn.hippo4j.common.executor;

import cn.hippo4j.common.design.builder.ThreadFactoryBuilder;
import cn.hippo4j.common.toolkit.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public final class ExecutorFactoryTest {

    ThreadFactory threadFactory = new ThreadFactoryBuilder().prefix("test").build();

    /**
     * 生成数据范围最小值
     */
    Integer rangeMin = 1;
    /**
     * 生成数据范围最大值
     */
    Integer rangeMax = 10;
    /**
     * 默认测试索引
     */
    Integer defaultIndex = 0;

    @Test
    public void assertNewSingleScheduledExecutorService() {
        ScheduledExecutorService executorService = ExecutorFactory.Managed.newSingleScheduledExecutorService(String.format("test-group-%s", defaultIndex), threadFactory);

        Assert.assertNotNull(executorService);

        ThreadPoolManager poolManager = (ThreadPoolManager) ReflectUtil.getFieldValue(ExecutorFactory.Managed.class, "THREAD_POOL_MANAGER");
        String poolName = (String) ReflectUtil.getFieldValue(ExecutorFactory.Managed.class, "DEFAULT_NAMESPACE");
        Map<String, Map<String, Set<ExecutorService>>> manager = (Map<String, Map<String, Set<ExecutorService>>>) ReflectUtil.getFieldValue(poolManager, "resourcesManager");

        // test default init
        Assert.assertEquals(1, manager.size());

        // test multiple registrations
        IntStream.rangeClosed(rangeMin, rangeMax).forEach(index -> ExecutorFactory.Managed.newSingleScheduledExecutorService(String.format("test-group-%s", index), threadFactory));
        Assert.assertEquals(1, manager.size());

        // test group size
        Map<String, Set<ExecutorService>> relationMap = manager.get(poolName);
        Assert.assertEquals(11, relationMap.size());
        Map<String, Set<ExecutorService>> firstRelationMap = new HashMap<>(relationMap);
        // test group relation size
        firstRelationMap.forEach((k, v) -> Assert.assertEquals(1, v.size()));

        // test multiple cover registrations
        IntStream.rangeClosed(defaultIndex, rangeMax).forEach(index -> ExecutorFactory.Managed.newSingleScheduledExecutorService(String.format("test-group-%s", index), threadFactory));
        // test group size
        Assert.assertEquals(11, relationMap.size());
        Map<String, Set<ExecutorService>> secondRelationMap = manager.get(poolName);
        // test group relation size
        secondRelationMap.forEach((k, v) -> Assert.assertEquals(2, v.size()));
    }

}
