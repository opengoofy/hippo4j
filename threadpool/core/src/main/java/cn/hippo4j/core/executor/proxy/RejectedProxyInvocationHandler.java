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

package cn.hippo4j.core.executor.proxy;

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.alarm.api.ThreadPoolCheckAlarm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rejected proxy invocation handler.
 */
@Slf4j
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    /**
     * Target object
     */
    private final Object target;

    /**
     * Thread-pool id
     */
    private final String threadPoolId;

    /**
     * Reject count
     */
    private final AtomicLong rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();
        if (ApplicationContextHolder.getInstance() != null) {
            try {
                ThreadPoolCheckAlarm alarmHandler = ApplicationContextHolder.getBean(ThreadPoolCheckAlarm.class);
                alarmHandler.asyncSendRejectedAlarm(threadPoolId);
            } catch (Throwable ex) {
                log.error("Failed to send rejection policy alert.", ex);
            }
        }
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
