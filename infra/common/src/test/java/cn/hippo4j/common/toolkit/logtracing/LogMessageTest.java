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

package cn.hippo4j.common.toolkit.logtracing;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogMessageTest {

    private final static String MESSAGE = "message";
    private final static String THROWABLE_MESSAGE = "throwable message";

    private LogMessage logMessage;

    @BeforeEach
    public void init() {
        logMessage = LogMessage.getInstance();
    }

    @Test
    void getInstance_shouldReturnANewLogMessageInstance() {
        final LogMessage newInstance = LogMessage.getInstance();
        assertNotNull(newInstance);
        assertNotSame(logMessage, newInstance);
    }

    @Test
    void getInstance_shouldHaveAnEmptyMessage() {
        assertEquals(Strings.EMPTY, logMessage.toString());
    }

    @Test
    void setMsg_shouldSetAnewMessageInLogMessage() {
        logMessage.setMsg(MESSAGE);
        assertEquals(MESSAGE, logMessage.toString());
    }

    @Test
    void msg_shouldContainsMessageAndThrowableMessage() {
        final String message = logMessage.msg(MESSAGE, new Throwable(THROWABLE_MESSAGE));
        assertNotNull(message);
        assertTrue(message.contains(MESSAGE));
        assertTrue(message.contains(THROWABLE_MESSAGE));
    }

    @Test
    void toString_shouldPrintKeyAndValueWhenSet() {
        logMessage.kv("key", "value");
        assertEquals("key=value", logMessage.toString());
    }

    @Test
    void toString_shouldPrintAllKeyAndValuePairs() {
        logMessage.kv("key1", "value1");
        logMessage.kv("key2", "value2");
        assertEquals("key1=value1||key2=value2", logMessage.toString());
    }

    @Test
    void toString_shouldPrintMessageAndAllKeyAndValuePairs() {
        logMessage.setMsg(MESSAGE);
        logMessage.kv("key1", "value1");
        logMessage.kv("key2", "value2");
        assertEquals("messagekey1=value1||key2=value2", logMessage.toString());
    }

    @Test
    void kv2String_shouldPrintMessageAndAllKeyAndValuePairs() {
        String result = logMessage.kv2String("key", "value");
        assertEquals("key=value", result);
    }
}