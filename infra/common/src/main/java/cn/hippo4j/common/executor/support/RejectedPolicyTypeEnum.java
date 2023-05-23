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

package cn.hippo4j.common.executor.support;

import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import lombok.Getter;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * Rejected policy type Enum.
 */
public enum RejectedPolicyTypeEnum {

    CALLER_RUNS_POLICY(1, "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),

    ABORT_POLICY(2, "AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),

    DISCARD_POLICY(3, "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),

    DISCARD_OLDEST_POLICY(4, "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy()),

    RUNS_OLDEST_TASK_POLICY(5, "RunsOldestTaskPolicy", new RunsOldestTaskPolicy()),

    SYNC_PUT_QUEUE_POLICY(6, "SyncPutQueuePolicy", new SyncPutQueuePolicy());

    @Getter
    private Integer type;

    @Getter
    private String name;

    private RejectedExecutionHandler rejectedHandler;

    RejectedPolicyTypeEnum(Integer type, String name, RejectedExecutionHandler rejectedHandler) {
        this.type = type;
        this.name = name;
        this.rejectedHandler = rejectedHandler;
    }

    static {
        ServiceLoaderRegistry.register(CustomRejectedExecutionHandler.class);
    }

    public static RejectedExecutionHandler createPolicy(String name) {
        RejectedPolicyTypeEnum rejectedTypeEnum = Stream.of(RejectedPolicyTypeEnum.values())
                .filter(each -> Objects.equals(each.name, name))
                .findFirst()
                .orElse(null);
        if (rejectedTypeEnum != null) {
            return rejectedTypeEnum.rejectedHandler;
        }
        Collection<CustomRejectedExecutionHandler> customRejectedExecutionHandlers = ServiceLoaderRegistry
                .getSingletonServiceInstances(CustomRejectedExecutionHandler.class);
        Optional<RejectedExecutionHandler> customRejected = customRejectedExecutionHandlers.stream()
                .filter(each -> Objects.equals(name, each.getName()))
                .map(each -> each.generateRejected())
                .findFirst();
        return customRejected.orElse(ABORT_POLICY.rejectedHandler);
    }

    public static RejectedExecutionHandler createPolicy(int type) {
        Optional<RejectedExecutionHandler> rejectedTypeEnum = Stream.of(RejectedPolicyTypeEnum.values())
                .filter(each -> Objects.equals(type, each.type))
                .map(each -> each.rejectedHandler)
                .findFirst();
        RejectedExecutionHandler resultRejected = rejectedTypeEnum.orElseGet(() -> {
            Collection<CustomRejectedExecutionHandler> customRejectedExecutionHandlers = ServiceLoaderRegistry
                    .getSingletonServiceInstances(CustomRejectedExecutionHandler.class);
            Optional<RejectedExecutionHandler> customRejected = customRejectedExecutionHandlers.stream()
                    .filter(each -> Objects.equals(type, each.getType()))
                    .map(each -> each.generateRejected())
                    .findFirst();
            return customRejected.orElse(ABORT_POLICY.rejectedHandler);
        });
        return resultRejected;
    }

    public static String getRejectedNameByType(int type) {
        return createPolicy(type).getClass().getSimpleName();
    }

    public static RejectedPolicyTypeEnum getRejectedPolicyTypeEnumByName(String name) {
        Optional<RejectedPolicyTypeEnum> rejectedTypeEnum = Stream.of(RejectedPolicyTypeEnum.values())
                .filter(each -> each.name.equals(name))
                .findFirst();
        return rejectedTypeEnum.orElse(ABORT_POLICY);
    }
}
