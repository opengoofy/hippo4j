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

package cn.hippo4j.config.monitor;

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Query monitor execute choose.
 */
@Component
public class QueryMonitorExecuteChoose implements CommandLineRunner {

    /**
     * Storage monitoring data execution container
     */
    private Map<String, AbstractMonitorDataExecuteStrategy> monitorDataExecuteStrategyChooseMap = new HashMap<>();

    /**
     * Choose by {@link cn.hippo4j.common.monitor.MessageTypeEnum}.
     *
     * @param messageTypeEnum {@link cn.hippo4j.common.monitor.MessageTypeEnum}
     * @return
     */
    public AbstractMonitorDataExecuteStrategy choose(MessageTypeEnum messageTypeEnum) {
        return choose(messageTypeEnum.name());
    }

    /**
     * Choose by mark type.
     *
     * @param markType {@link cn.hippo4j.common.monitor.MessageTypeEnum#name()}
     * @return
     */
    public AbstractMonitorDataExecuteStrategy choose(String markType) {
        AbstractMonitorDataExecuteStrategy executeStrategy = monitorDataExecuteStrategyChooseMap.get(markType);
        if (executeStrategy == null) {
            executeStrategy = monitorDataExecuteStrategyChooseMap.get(MessageTypeEnum.DEFAULT.name());
        }
        return executeStrategy;
    }

    /**
     * Choose and execute.
     *
     * @param message {@link Message}
     */
    public void chooseAndExecute(Message message) {
        MessageTypeEnum messageType = message.getMessageType();
        AbstractMonitorDataExecuteStrategy executeStrategy = choose(messageType);
        executeStrategy.execute(message);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, AbstractMonitorDataExecuteStrategy> monitorDataExecuteStrategyMap =
                ApplicationContextHolder.getBeansOfType(AbstractMonitorDataExecuteStrategy.class);
        monitorDataExecuteStrategyMap.values().forEach(each -> monitorDataExecuteStrategyChooseMap.put(each.mark(), each));
    }
}
