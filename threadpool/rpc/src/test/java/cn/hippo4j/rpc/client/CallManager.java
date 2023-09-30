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

package cn.hippo4j.rpc.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class CallManager {

    public int call() {
        return 1;
    }

    public int call(Integer a) {
        return a;
    }

    public int call(Integer a, Integer b) {
        return a + b;
    }

    public int callTestTimeout() {
        // thread sleep for 10 seconds
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10L));
        return 1;
    }

}
