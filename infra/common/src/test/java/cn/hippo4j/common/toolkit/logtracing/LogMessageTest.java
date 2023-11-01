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
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogMessageTest {

    private final static String MESSAGE = "message";
    private final static String THROWABLE_MESSAGE = "throwable message";
    private LogMessage logMessage;

    @Before
    public void init() {
        logMessage = LogMessage.getInstance();
    }

    @Test
    public void testGetInstanceShouldReturnANewLogMessageInstance() {
        final LogMessage newInstance = LogMessage.getInstance();
        assertNotNull(newInstance);
        assertNotSame(logMessage, newInstance);
    }

    @Test
    public void testToStringShouldHaveAnEmptyMessage() {
        assertEquals(Strings.EMPTY, logMessage.toString());
    }

    @Test
    public void testSetMsgShouldSetAnewMessageInLogMessage() {
        logMessage.setMsg(MESSAGE);
        assertEquals(MESSAGE, logMessage.toString());
    }

    @Test
    public void testMsgShouldContainsMessageAndThrowableMessage() {
        final String message = logMessage.msg(MESSAGE, new Throwable(THROWABLE_MESSAGE));
        assertNotNull(message);
        assertTrue(message.contains(MESSAGE));
        assertTrue(message.contains(THROWABLE_MESSAGE));
    }

    @Test
    public void testKvShouldPutKeyAndValue() {
        logMessage.kv("key", "value");
        assertEquals("key=value", logMessage.toString());
    }

    @Test
    public void testKvShouldPutAllKeyAndValuePairs() {
        logMessage.kv("key1", "value1");
        logMessage.kv("key2", "value2");
        String output = logMessage.toString();
        assertTrue(output.equals("key1=value1||key2=value2") || output.equals("key2=value2||key1=value1"));
    }

    @Test
    public void testToStringShouldPrintMessageAndAllKeyAndValuePairs() {
        logMessage.setMsg(MESSAGE);
        logMessage.kv("key1", "value1");
        logMessage.kv("key2", "value2");
        String output = logMessage.toString();
        assertTrue(output.equals("messagekey1=value1||key2=value2") || output.equals("messagekey2=value2||key1=value1"));
    }

    @Test
    public void testKv2StringShouldPrintMessageAndAllKeyAndValuePairs() {
        String result = logMessage.kv2String("key", "value");
        assertEquals("key=value", result);
    }
}