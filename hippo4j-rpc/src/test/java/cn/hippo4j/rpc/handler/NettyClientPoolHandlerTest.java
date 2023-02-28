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

import io.netty.channel.ChannelHandler;
import org.junit.Assert;
import org.junit.Test;

public class NettyClientPoolHandlerTest {

    @Test
    public void testGetHandlerEntity() {
        TestHandler handler = new TestHandler();
        long order = 0;
        String name = "Test";
        NettyClientPoolHandler poolHandler = new NettyClientPoolHandler();
        HandlerManager.HandlerEntity<ChannelHandler> entity = poolHandler.getHandlerEntity(order, handler, name);
        Assert.assertEquals(entity.getName(), name);
        Assert.assertEquals(entity.getOrder(), order);
        Assert.assertEquals(entity.getHandler(), handler);
    }

    @Test
    public void testCompareTo() {
        TestHandler handler = new TestHandler();
        long order = 0;
        String name = "Test";
        TestHandler handler1 = new TestHandler();
        long order1 = 1;
        String name1 = "Test1";
        NettyClientPoolHandler poolHandler = new NettyClientPoolHandler();
        HandlerManager.HandlerEntity<ChannelHandler> entity = poolHandler.getHandlerEntity(order, handler, name);
        HandlerManager.HandlerEntity<ChannelHandler> entity1 = poolHandler.getHandlerEntity(order1, handler1, name1);
        int compare = entity.compareTo(entity1);
        Assert.assertTrue(compare < 0);
    }

    @Test
    public void addLast() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addLast(new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void addFirst() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addFirst(new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void testAddLast() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addLast("Test", new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void testAddFirst() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addFirst("Test", new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }
}