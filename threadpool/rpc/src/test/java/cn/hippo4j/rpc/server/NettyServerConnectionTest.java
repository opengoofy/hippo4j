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

package cn.hippo4j.rpc.server;

import cn.hippo4j.rpc.connection.SimpleServerConnection;
import cn.hippo4j.rpc.handler.TestHandler;
import org.junit.Assert;
import org.junit.Test;

public class NettyServerConnectionTest {

    static final String test = "Test";

    @Test
    public void addLast() {
        try (SimpleServerConnection connection = new SimpleServerConnection()) {
            Assert.assertTrue(connection.isEmpty());
            connection.addLast(null, new TestHandler());
            Assert.assertFalse(connection.isEmpty());
        }
    }

    @Test
    public void addFirst() {
        try (SimpleServerConnection connection = new SimpleServerConnection()) {
            Assert.assertTrue(connection.isEmpty());
            connection.addFirst(null, new TestHandler());
            Assert.assertFalse(connection.isEmpty());
        }
    }

    @Test
    public void testAddLast() {
        try (SimpleServerConnection connection = new SimpleServerConnection()) {
            Assert.assertTrue(connection.isEmpty());
            connection.addLast(test, new TestHandler());
            Assert.assertFalse(connection.isEmpty());
        }
    }

    @Test
    public void testAddFirst() {
        try (SimpleServerConnection connection = new SimpleServerConnection()) {
            Assert.assertTrue(connection.isEmpty());
            connection.addFirst(test, new TestHandler());
            Assert.assertFalse(connection.isEmpty());
        }
    }
}