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

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class ResultHolderTest {

    static final String str1 = "1";
    static final String str2 = "2";

    @Test
    public void test() {
        String s1 = UUID.randomUUID().toString();
        String o1 = s1 + str1;
        String s2 = UUID.randomUUID().toString();
        String o2 = s2 + str2;

        ResultHolder.put(s1, o1);
        ResultHolder.put(s2, o2);

        Object r1 = ResultHolder.get(s1);
        Object r2 = ResultHolder.get(s2);

        Assert.assertEquals(r1, o1);
        Assert.assertEquals(r2, o2);
    }

    @Test
    public void testThread() throws InterruptedException {
        AtomicInteger a = new AtomicInteger();
        String s1 = UUID.randomUUID().toString();
        String o1 = s1 + str1;
        CompletableFuture.runAsync(() -> {
            ResultHolder.putThread(o1, Thread.currentThread());
            LockSupport.park();
            a.set(1);
        });
        Assert.assertEquals(0, a.get());
        TimeUnit.SECONDS.sleep(1);
        ResultHolder.wake(o1);
        TimeUnit.SECONDS.sleep(1);
        Assert.assertEquals(1, a.get());
    }
}