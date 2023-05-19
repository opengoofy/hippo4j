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

import cn.hippo4j.common.toolkit.MapUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public final class ExecutorFactoryTest {

    ThreadFactory threadFactory = new ThreadFactoryBuilder().prefix("test").build();

    /**
     * data range min
     */
    Integer rangeMin = 1;
    /**
     * data range max
     */
    Integer rangeMax = 10;
    /**
     * default test index
     */
    Integer defaultIndex = 0;

    @Test
    public void assertNewSingleScheduledExecutorService() {
        // init data snapshot
        ThreadPoolManager poolManager = (ThreadPoolManager) ReflectUtil.getFieldValue(ExecutorFactory.Managed.class, "THREAD_POOL_MANAGER");
        String poolName = (String) ReflectUtil.getFieldValue(ExecutorFactory.Managed.class, "DEFAULT_NAMESPACE");
        Map<String, Map<String, Set<ExecutorService>>> manager = (Map<String, Map<String, Set<ExecutorService>>>) ReflectUtil.getFieldValue(poolManager, "resourcesManager");
        Map<String, Set<ExecutorService>> initRelationMap = manager.get(poolName);
        int defaultManagerSize = manager.size();
        int defaultRelationSize = MapUtil.isEmpty(initRelationMap) ? 0 : initRelationMap.size();

        // test begin
        ScheduledExecutorService executorService = ExecutorFactory.Managed.newSingleScheduledExecutorService(String.format("test-group-%s", defaultIndex), threadFactory);

        Assert.assertNotNull(executorService);

        // check default init
        Assert.assertEquals(1, manager.size() - defaultManagerSize);

        // check multiple registrations and check to see if it is still an instance
        IntStream.rangeClosed(rangeMin, rangeMax).forEach(index -> ExecutorFactory.Managed.newSingleScheduledExecutorService(String.format("test-group-%s", index), threadFactory));
        Assert.assertEquals(1, manager.size() - defaultManagerSize);

        // check group size
        Map<String, Set<ExecutorService>> relationMap = manager.get(poolName);
        Assert.assertEquals(11, relationMap.size() - defaultRelationSize);
        // check the number of threads between the group and the thread pool
        IntStream.rangeClosed(rangeMin, rangeMax).forEach(index -> {
            String relationKey = String.format("test-group-%s", index);
            Assert.assertNotNull(relationMap.get(relationKey));
            Assert.assertEquals(1, relationMap.get(relationKey).size());
        });

        // instantiate the same group a second time and check the corresponding quantitative relationship
        IntStream.rangeClosed(defaultIndex, rangeMax).forEach(index -> ExecutorFactory.Managed.newSingleScheduledExecutorService(String.format("test-group-%s", index), threadFactory));
        // chek group size
        Assert.assertEquals(11, manager.get(poolName).size() - defaultRelationSize);
        // check the number of threads between the group and the thread pool
        IntStream.rangeClosed(rangeMin, rangeMax).forEach(index -> Assert.assertEquals(2, relationMap.get(String.format("test-group-%s", index)).size()));
    }

}
