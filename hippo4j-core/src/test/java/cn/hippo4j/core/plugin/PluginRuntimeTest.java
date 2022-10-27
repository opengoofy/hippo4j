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

package cn.hippo4j.core.plugin;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.plugin.impl.TaskTimeRecordPlugin;
import cn.hippo4j.core.plugin.impl.ThreadPoolExecutorShutdownPlugin;
import cn.hippo4j.core.plugin.manager.DefaultThreadPoolPluginManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * test {@link ThreadPoolPlugin}'s info to json
 */
public class PluginRuntimeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Test
    public void testGetPluginRuntime() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());

        // TaskRejectCountRecordPlugin
        TaskRejectCountRecordPlugin taskRejectCountRecordPlugin = new TaskRejectCountRecordPlugin();
        executor.register(taskRejectCountRecordPlugin);

        // TaskRejectCountRecordPlugin
        TaskTimeRecordPlugin taskTimeRecordPlugin = new TaskTimeRecordPlugin();
        executor.register(taskTimeRecordPlugin);

        // ThreadPoolExecutorShutdownPlugin
        ThreadPoolExecutorShutdownPlugin executorShutdownPlugin = new ThreadPoolExecutorShutdownPlugin(2000L, true);
        executor.register(executorShutdownPlugin);

        executor.submit(() -> ThreadUtil.sleep(100L));
        executor.submit(() -> ThreadUtil.sleep(300L));
        executor.submit(() -> ThreadUtil.sleep(200L));

        ThreadUtil.sleep(1000L);
        List<PluginRuntime> runtimeList = executor.getAllPlugins().stream()
                .map(ThreadPoolPlugin::getPluginRuntime)
                .collect(Collectors.toList());
        Assert.assertEquals(3, runtimeList.size());

        System.out.println(objectMapper.writeValueAsString(runtimeList));
    }

}
