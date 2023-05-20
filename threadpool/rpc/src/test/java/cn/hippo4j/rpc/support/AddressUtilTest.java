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

package cn.hippo4j.rpc.support;

import cn.hippo4j.rpc.exception.ConnectionException;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;

public class AddressUtilTest {

    String address1 = "http://hippo4j.cn/login:8080";
    String address2 = "https://hippo4j.cn/login:8080";
    String address3 = "https://hippo4j.cn/login";
    String addressHostName = "hippo4j.cn/login";
    int addressPort = 8080;

    @Test
    public void test() {
        InetSocketAddress address = AddressUtil.getInetAddress(address1);
        Assert.assertEquals(addressHostName, address.getHostName());
        Assert.assertEquals(addressPort, address.getPort());
    }

    @Test
    public void testAddress2() {
        InetSocketAddress address = AddressUtil.getInetAddress(address2);
        Assert.assertEquals(addressHostName, address.getHostName());
        Assert.assertEquals(addressPort, address.getPort());
    }

    @Test(expected = ConnectionException.class)
    public void testAddress3() {
        AddressUtil.getInetAddress(address3);
    }
}
