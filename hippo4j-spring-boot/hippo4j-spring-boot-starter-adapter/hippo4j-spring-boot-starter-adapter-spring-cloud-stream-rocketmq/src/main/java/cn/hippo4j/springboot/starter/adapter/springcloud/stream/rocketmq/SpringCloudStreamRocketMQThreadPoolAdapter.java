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

package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rocketmq;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * Spring cloud stream rocketMQ thread-pool adapter.
 */
@Slf4j
public class SpringCloudStreamRocketMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private final Map<String, ThreadPoolExecutor> ROCKET_MQ_SPRING_CLOUD_STREAM_CONSUME_EXECUTOR = Maps.newHashMap();

    @Override
    public String mark() {
        return "RocketMQ_SpringCloud_Stream";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolStateInfo(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        ThreadPoolExecutor rocketMQConsumeExecutor = ROCKET_MQ_SPRING_CLOUD_STREAM_CONSUME_EXECUTOR.get(identify);
        if (rocketMQConsumeExecutor != null) {
            result.setCoreSize(rocketMQConsumeExecutor.getCorePoolSize());
            result.setMaximumSize(rocketMQConsumeExecutor.getMaximumPoolSize());
            return result;
        }
        log.warn("[{}] RocketMQ consuming thread pool not found.", identify);
        return result;
    }

    @Override
    public boolean updateThreadPool(String identify, ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        ThreadPoolExecutor rocketMQConsumeExecutor = ROCKET_MQ_SPRING_CLOUD_STREAM_CONSUME_EXECUTOR.get(identify);
        if (rocketMQConsumeExecutor != null) {
            int originalCoreSize = rocketMQConsumeExecutor.getCorePoolSize();
            int originalMaximumPoolSize = rocketMQConsumeExecutor.getMaximumPoolSize();
            rocketMQConsumeExecutor.setCorePoolSize(threadPoolAdapterParameter.getCoreSize());
            rocketMQConsumeExecutor.setMaximumPoolSize(threadPoolAdapterParameter.getMaximumSize());
            log.info("[{}] RocketMQ consumption thread pool parameter change. coreSize :: {}, maximumSize :: {}",
                    identify,
                    String.format(CHANGE_DELIMITER, originalCoreSize, rocketMQConsumeExecutor.getCorePoolSize()),
                    String.format(CHANGE_DELIMITER, originalMaximumPoolSize, rocketMQConsumeExecutor.getMaximumPoolSize()));
            return true;
        }
        log.warn("[{}] RocketMQ consuming thread pool not found.", identify);
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // TODO Get rocketMQ consumer thread pool collection
    }
}
