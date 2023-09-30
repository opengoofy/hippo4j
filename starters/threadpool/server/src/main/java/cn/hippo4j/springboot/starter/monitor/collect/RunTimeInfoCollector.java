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

package cn.hippo4j.springboot.starter.monitor.collect;

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.monitor.AbstractMessage;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static cn.hippo4j.core.toolkit.IdentifyUtil.getThreadPoolIdentify;

/**
 * Thread pool runtime data collection.
 */
@AllArgsConstructor
public class RunTimeInfoCollector extends AbstractThreadPoolRuntime implements Collector {

    private final BootstrapProperties properties;

    @Override
    public Message collectMessage() {
        AbstractMessage message = new RuntimeMessage();
        List<Message> runtimeMessages = new ArrayList<>();
        List<String> listThreadPoolId = ThreadPoolExecutorRegistry.listThreadPoolExecutorId();
        for (String each : listThreadPoolId) {
            ThreadPoolRunStateInfo poolRunState = getPoolRunState(each);
            RuntimeMessage runtimeMessage = BeanUtil.convert(poolRunState, RuntimeMessage.class);
            runtimeMessage.setGroupKey(getThreadPoolIdentify(each, properties.getItemId(), properties.getNamespace()));
            runtimeMessages.add(runtimeMessage);
        }
        message.setMessageType(MessageTypeEnum.RUNTIME);
        message.setMessages(runtimeMessages);
        return message;
    }

    @Override
    public ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo threadPoolRunStateInfo) {
        return threadPoolRunStateInfo;
    }
}
