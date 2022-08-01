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

package cn.hippo4j.monitor.log;

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import com.example.monitor.base.AbstractDynamicThreadPoolMonitor;
import lombok.extern.slf4j.Slf4j;

/**
 * Log monitor handler
 */
@Slf4j
public class LogMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    public LogMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        super(threadPoolRunStateHandler);
    }

    @Override
    protected void execute(ThreadPoolRunStateInfo poolRunStateInfo) {
        log.info("{}", JSONUtil.toJSONString(poolRunStateInfo));
    }

    @Override
    public String getType() {
        return "log";
    }
}
