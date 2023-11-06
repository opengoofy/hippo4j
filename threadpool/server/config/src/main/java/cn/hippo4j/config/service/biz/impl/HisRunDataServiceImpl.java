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

package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.model.Result;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.DateUtil;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.toolkit.MessageConvert;
import cn.hippo4j.config.config.ServerBootstrapProperties;
import cn.hippo4j.config.mapper.HisRunDataMapper;
import cn.hippo4j.config.model.HisRunDataInfo;
import cn.hippo4j.config.model.biz.monitor.MonitorActiveRespDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorQueryReqDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorRespDTO;
import cn.hippo4j.config.monitor.QueryMonitorExecuteChoose;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.service.biz.HisRunDataService;
import cn.hippo4j.server.common.base.Results;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_0;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_1;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_2;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_3;
import static cn.hippo4j.common.toolkit.DateUtil.NORM_TIME_PATTERN;

/**
 * His run data service impl.
 */
@Service
@AllArgsConstructor
public class HisRunDataServiceImpl extends ServiceImpl<HisRunDataMapper, HisRunDataInfo> implements HisRunDataService {

    private final ServerBootstrapProperties properties;

    private final QueryMonitorExecuteChoose queryMonitorExecuteChoose;

    private final ThreadPoolTaskExecutor monitorThreadPoolTaskExecutor;

    @Override
    public List<MonitorRespDTO> query(MonitorQueryReqDTO reqDTO) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime dateTime = currentDate.plusMinutes(-properties.getCleanHistoryDataPeriod());
        long startTime = DateUtil.getTime(dateTime);
        List<HisRunDataInfo> hisRunDataInfos = this.lambdaQuery()
                .eq(HisRunDataInfo::getTenantId, reqDTO.getTenantId())
                .eq(HisRunDataInfo::getItemId, reqDTO.getItemId())
                .eq(HisRunDataInfo::getTpId, reqDTO.getTpId())
                .eq(HisRunDataInfo::getInstanceId, reqDTO.getInstanceId())
                .between(HisRunDataInfo::getTimestamp, startTime, DateUtil.getTime(currentDate))
                .orderByAsc(HisRunDataInfo::getTimestamp)
                .list();
        return BeanUtil.convert(hisRunDataInfos, MonitorRespDTO.class);
    }

    @Override
    public MonitorActiveRespDTO queryInfoThreadPoolMonitor(MonitorQueryReqDTO reqDTO) {
        Long startTime = reqDTO.getStartTime();
        Long endTime = reqDTO.getEndTime();
        if (startTime == null || endTime == null) {
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime dateTime = currentDate.plusMinutes(-properties.getCleanHistoryDataPeriod());
            startTime = DateUtil.getTime(dateTime);
            endTime = DateUtil.getTime(currentDate);
        }
        List<HisRunDataInfo> hisRunDataInfos = this.lambdaQuery()
                .eq(HisRunDataInfo::getTenantId, reqDTO.getTenantId())
                .eq(HisRunDataInfo::getItemId, reqDTO.getItemId())
                .eq(HisRunDataInfo::getTpId, reqDTO.getTpId())
                .eq(HisRunDataInfo::getInstanceId, reqDTO.getInstanceId())
                .between(HisRunDataInfo::getTimestamp, startTime, endTime)
                .orderByAsc(HisRunDataInfo::getTimestamp)
                .list();
        List<String> times = new ArrayList<>();
        List<Long> poolSizeList = new ArrayList<>();
        List<Long> activeSizeList = new ArrayList<>();
        List<Long> queueCapacityList = new ArrayList<>();
        List<Long> queueSizeList = new ArrayList<>();
        List<Long> rangeCompletedTaskCountList = new ArrayList<>();
        List<Long> completedTaskCountList = new ArrayList<>();
        List<Long> rangeRejectCountList = new ArrayList<>();
        List<Long> rejectCountList = new ArrayList<>();
        List<Long> queueRemainingCapacityList = new ArrayList<>();
        long completedTaskCountTemp = 0L;
        long rejectCountTemp = 0L;
        boolean firstFlag = true;
        for (HisRunDataInfo each : hisRunDataInfos) {
            String time = DateUtil.format(new Date(each.getTimestamp()), NORM_TIME_PATTERN);
            times.add(time);
            poolSizeList.add(each.getPoolSize());
            activeSizeList.add(each.getActiveSize());
            queueSizeList.add(each.getQueueSize());
            queueRemainingCapacityList.add(each.getQueueRemainingCapacity());
            queueCapacityList.add(each.getQueueCapacity());
            if (firstFlag) {
                firstFlag = false;
                completedTaskCountList.add(0L);
                completedTaskCountTemp = each.getCompletedTaskCount();
                rejectCountTemp = each.getRejectCount();
                continue;
            }
            rangeCompletedTaskCountList.add(each.getCompletedTaskCount() - completedTaskCountTemp);
            completedTaskCountList.add(each.getCompletedTaskCount());
            rangeRejectCountList.add(each.getRejectCount() - rejectCountTemp);
            rejectCountList.add(each.getRejectCount());
            completedTaskCountTemp = each.getCompletedTaskCount();
            rejectCountTemp = each.getRejectCount();
        }
        return MonitorActiveRespDTO.builder()
                .times(times)
                .poolSizeList(poolSizeList)
                .activeSizeList(activeSizeList)
                .queueSizeList(queueSizeList)
                .queueCapacityList(queueCapacityList)
                .rangeRejectCountList(rangeRejectCountList)
                .rejectCountList(rejectCountList)
                .completedTaskCountList(completedTaskCountList)
                .rangeCompletedTaskCountList(rangeCompletedTaskCountList)
                .queueRemainingCapacityList(queueRemainingCapacityList)
                .build();
    }

    @Override
    public MonitorRespDTO queryThreadPoolLastTaskCount(MonitorQueryReqDTO reqDTO) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime dateTime = currentDate.plusMinutes(-properties.getCleanHistoryDataPeriod());
        long startTime = DateUtil.getTime(dateTime);
        HisRunDataInfo hisRunDataInfo = this.lambdaQuery()
                .eq(HisRunDataInfo::getTenantId, reqDTO.getTenantId())
                .eq(HisRunDataInfo::getItemId, reqDTO.getItemId())
                .eq(HisRunDataInfo::getTpId, reqDTO.getTpId())
                .eq(HisRunDataInfo::getInstanceId, reqDTO.getInstanceId())
                .orderByDesc(HisRunDataInfo::getTimestamp)
                .between(HisRunDataInfo::getTimestamp, startTime, DateUtil.getTime(currentDate))
                .last("LIMIT 1")
                .one();
        return BeanUtil.convert(hisRunDataInfo, MonitorRespDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Message message) {
        List<RuntimeMessage> runtimeMessages = message.getMessages();
        List<HisRunDataInfo> hisRunDataInfos = new ArrayList<>();
        runtimeMessages.forEach(each -> {
            HisRunDataInfo hisRunDataInfo = BeanUtil.convert(each, HisRunDataInfo.class);
            String[] parseKey = GroupKey.parseKey(each.getGroupKey());
            boolean checkFlag = ConfigCacheService.checkTpId(each.getGroupKey(), parseKey[INDEX_0], parseKey[INDEX_3]);
            if (checkFlag) {
                hisRunDataInfo.setTpId(parseKey[INDEX_0]);
                hisRunDataInfo.setItemId(parseKey[INDEX_1]);
                hisRunDataInfo.setTenantId(parseKey[INDEX_2]);
                hisRunDataInfo.setInstanceId(parseKey[INDEX_3]);
                hisRunDataInfos.add(hisRunDataInfo);
            }
        });
        this.saveBatch(hisRunDataInfos);
    }

    @Override
    public Result<Void> dataCollect(MessageWrapper messageWrapper) {
        Runnable task = () -> {
            Message message = MessageConvert.convert(messageWrapper);
            queryMonitorExecuteChoose.chooseAndExecute(message);
        };
        try {
            monitorThreadPoolTaskExecutor.execute(task);
        } catch (Exception ex) {
            log.error("Monitoring data insertion database task overflow.", ex);
        }
        return Results.success();
    }
}
