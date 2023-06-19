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

package cn.hippo4j.agent.core.util;

public interface ThreadPoolPropertyKey {

    String THREAD_POOL_ID = "threadPoolId";

    String CORE_POOL_SIZE = "corePoolSize";

    String MAXIMUM_POOL_SIZE = "maximumPoolSize";

    String ALLOW_CORE_THREAD_TIME_OUT = "allowCoreThreadTimeOut";

    String KEEP_ALIVE_TIME = "keepAliveTime";

    String BLOCKING_QUEUE = "blockingQueue";

    String QUEUE_CAPACITY = "queueCapacity";

    String REJECTED_HANDLER = "rejectedHandler";

    String EXECUTE_TIME_OUT = "executeTimeOut";
}
