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

package cn.hippo4j.adapter.web;

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.ByteConvertUtil;
import cn.hippo4j.common.toolkit.MemoryUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import lombok.extern.slf4j.Slf4j;

/**
 * Web thread pool run state handler.
 */
@Slf4j
public class WebThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    @Override
    public ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo poolRunStateInfo) {
        long used = MemoryUtil.heapMemoryUsed();
        long max = MemoryUtil.heapMemoryMax();
        String memoryProportion = StringUtil.newBuilder(
                "Allocation: ",
                ByteConvertUtil.getPrintSize(used),
                " / Maximum available: ",
                ByteConvertUtil.getPrintSize(max));
        poolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad() + "%");
        poolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad() + "%");
        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(Math.subtractExact(max, used)));
        return poolRunStateInfo;
    }
}
