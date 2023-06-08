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

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.monitor.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/***
 * @description : Todo
 * @author : DDDreame
 * @date : 2023/5/27 23:24 
 */
public class MessageConvertTest {

    @Test
    public void testConvert() {
        AbstractMessage message = new RuntimeMessage();
        List<Message> runtimeMessages = new ArrayList<>();
        ThreadPoolRunStateInfo poolRunState = ThreadPoolRunStateInfo.builder()
                .tpId("testTPid")
                .activeSize(4)
                .poolSize(12)
                .completedTaskCount(8L)
                .largestPoolSize(12)
                .currentLoad("6")
                .clientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .peakLoad("20")
                .queueSize(0)
                .queueRemainingCapacity(512)
                .rejectCount(0L)
                .timestamp(System.currentTimeMillis())
                .build();
        RuntimeMessage runtimeMessage = BeanUtil.convert(poolRunState, RuntimeMessage.class);
        runtimeMessage.setGroupKey("test-groupKeys");
        runtimeMessages.add(runtimeMessage);

        message.setMessageType(MessageTypeEnum.RUNTIME);
        message.setMessages(runtimeMessages);
        MessageWrapper messageWrapper = MessageConvert.convert(message);
        Assertions.assertNotNull(messageWrapper);
    }

    @Test
    public void testMessageWrapperConvert() {
        AbstractMessage message = new RuntimeMessage();
        List<Message> runtimeMessages = new ArrayList<>();
        ThreadPoolRunStateInfo poolRunState = ThreadPoolRunStateInfo.builder()
                .tpId("testTPid")
                .activeSize(4)
                .poolSize(12)
                .completedTaskCount(8L)
                .largestPoolSize(12)
                .currentLoad("6")
                .clientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .peakLoad("20")
                .queueSize(0)
                .queueRemainingCapacity(512)
                .rejectCount(0L)
                .timestamp(System.currentTimeMillis())
                .build();
        RuntimeMessage runtimeMessage = BeanUtil.convert(poolRunState, RuntimeMessage.class);
        runtimeMessage.setGroupKey("test-groupKeys");
        runtimeMessages.add(runtimeMessage);

        message.setMessageType(MessageTypeEnum.RUNTIME);
        message.setMessages(runtimeMessages);
        MessageWrapper messageWrapper = MessageConvert.convert(message);
        Message messageResult = MessageConvert.convert(messageWrapper);
        Assertions.assertNotNull(messageResult);
        Assertions.assertEquals(message, messageResult);
    }

    @Test
    public void testMessageWrapperConvertException() {
        Assertions.assertThrows(Exception.class, () -> {
            Map<String, Object> data1 = new HashMap<>();
            data1.put("key1", "value1");
            data1.put("key2", 123);
            Map<String, Object> data2 = new HashMap<>();
            data2.put("key3", true);
            data2.put("key4", 3.14);
            List<Map<String, Object>> contentParams = Arrays.asList(data1, data2);
            Class responseClass = String.class;
            MessageTypeEnum messageType = MessageTypeEnum.DEFAULT;
            MessageWrapper messageWrapper = new MessageWrapper(contentParams, responseClass, messageType);
            MessageConvert.convert(messageWrapper);
        });
    }
}
