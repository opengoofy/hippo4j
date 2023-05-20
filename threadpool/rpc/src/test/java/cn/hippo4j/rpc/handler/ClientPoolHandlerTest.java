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

package cn.hippo4j.rpc.handler;

import cn.hippo4j.rpc.exception.OperationException;
import io.netty.channel.ChannelHandler;
import org.junit.Assert;
import org.junit.Test;

public class ClientPoolHandlerTest {

    static final String test = "Test";
    static final String test1 = "Test1";

    @Test
    public void testGetHandlerEntity() {
        TestHandler handler = new TestHandler();
        long order = 0;
        String name = test;
        ClientPoolHandler poolHandler = new ClientPoolHandler();
        HandlerManager.HandlerEntity<ChannelHandler> entity = poolHandler.getHandlerEntity(order, handler, name);
        Assert.assertEquals(entity.getName(), name);
        Assert.assertEquals(entity.getOrder(), order);
        Assert.assertEquals(entity.getHandler(), handler);
    }

    @Test
    public void testCompareTo() {
        TestHandler handler = new TestHandler();
        long order = 0;
        TestHandler handler1 = new TestHandler();
        long order1 = 1;
        ClientPoolHandler poolHandler = new ClientPoolHandler();
        HandlerManager.HandlerEntity<ChannelHandler> entity = poolHandler.getHandlerEntity(order, handler, test);
        HandlerManager.HandlerEntity<ChannelHandler> entity1 = poolHandler.getHandlerEntity(order1, handler1, test1);
        int compare = entity.compareTo(entity1);
        Assert.assertTrue(compare < 0);
    }

    @Test
    public void addLast() {
        ClientPoolHandler handler = new ClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addLast(null, new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void addFirst() {
        ClientPoolHandler handler = new ClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addFirst(null, new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void testAddLast() {
        ClientPoolHandler handler = new ClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addLast(test, new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void testAddFirst() {
        ClientPoolHandler handler = new ClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addFirst(test, new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test(expected = OperationException.class)
    public void testGetHandlerEntityFalse() {
        TestFalseHandler handler = new TestFalseHandler();
        long order = 0;
        ClientPoolHandler poolHandler = new ClientPoolHandler();
        poolHandler.getHandlerEntity(order, handler, test);
    }

}