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

package cn.hippo4j.config.monitor;

import cn.hippo4j.common.executor.ExecutorFactory;
import cn.hippo4j.common.toolkit.DateUtil;
import cn.hippo4j.config.config.ServerBootstrapProperties;
import cn.hippo4j.config.model.HisRunDataInfo;
import cn.hippo4j.config.service.biz.HisRunDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.DEFAULT_GROUP;

/**
 * Regularly clean up the historical running data of thread pool.
 */
@Component
@RequiredArgsConstructor
public class TimeCleanHistoryDataTask implements Runnable, InitializingBean {

    @NonNull
    private final ServerBootstrapProperties properties;

    @NonNull
    private final HisRunDataService hisRunDataService;

    private ScheduledExecutorService cleanHistoryDataExecutor;

    @Override
    public void run() {
        LocalDateTime offsetMinuteDateTime = LocalDateTime.now().plusMinutes(-properties.getCleanHistoryDataPeriod());
        LambdaQueryWrapper<HisRunDataInfo> queryWrapper = Wrappers.lambdaQuery(HisRunDataInfo.class)
                .le(HisRunDataInfo::getTimestamp, DateUtil.getTime(offsetMinuteDateTime));
        hisRunDataService.remove(queryWrapper);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.getCleanHistoryDataEnable()) {
            cleanHistoryDataExecutor = ExecutorFactory.Managed
                    .newSingleScheduledExecutorService(DEFAULT_GROUP, r -> new Thread(r, "clean-history-data"));
            cleanHistoryDataExecutor.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MINUTES);
        }
    }
}
