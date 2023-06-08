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

package cn.hippo4j.message.service;

import cn.hippo4j.threadpool.message.api.AlarmControlDTO;
import cn.hippo4j.threadpool.message.api.NotifyTypeEnum;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import org.junit.Assert;
import org.junit.Test;

public final class AlarmControlHandlerTest {

    @Test
    public void assertIsNotSendAlarm() {
        AlarmControlHandler alarmControlHandler = new AlarmControlHandler();
        AlarmControlDTO alarmControlDTO = new AlarmControlDTO("1", "Wechat", NotifyTypeEnum.ACTIVITY);
        Assert.assertFalse(alarmControlHandler.isSendAlarm(alarmControlDTO));
    }

    @Test
    public void assertIsSendAlarm() {
        AlarmControlHandler alarmControlHandler = new AlarmControlHandler();
        AlarmControlDTO alarmControlDTO = new AlarmControlDTO("1", "Wechat", NotifyTypeEnum.ACTIVITY);
        alarmControlHandler.initCacheAndLock("1", "Wechat", 1);
        Assert.assertTrue(alarmControlHandler.isSendAlarm(alarmControlDTO));
    }
}
