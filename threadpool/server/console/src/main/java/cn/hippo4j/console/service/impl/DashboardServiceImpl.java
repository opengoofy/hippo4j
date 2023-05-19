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

package cn.hippo4j.console.service.impl;

import cn.hippo4j.common.extension.enums.DelEnum;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.DateUtil;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.mapper.HisRunDataMapper;
import cn.hippo4j.config.mapper.ItemInfoMapper;
import cn.hippo4j.config.mapper.TenantInfoMapper;
import cn.hippo4j.config.model.CacheItem;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ConfigInfoBase;
import cn.hippo4j.config.model.ItemInfo;
import cn.hippo4j.config.model.TenantInfo;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.console.model.ChartInfo;
import cn.hippo4j.console.model.LineChartInfo;
import cn.hippo4j.console.model.PieChartInfo;
import cn.hippo4j.console.model.RankingChart;
import cn.hippo4j.console.model.TenantChart;
import cn.hippo4j.console.service.DashboardService;
import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hippo4j.common.toolkit.ContentUtil.getGroupKey;

/**
 * Dashboard service impl.
 */
@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TenantInfoMapper tenantInfoMapper;

    private final ItemInfoMapper itemInfoMapper;

    private final ConfigInfoMapper configInfoMapper;

    private final HisRunDataMapper hisRunDataMapper;

    private final BaseInstanceRegistry baseInstanceRegistry;

    @Override
    public ChartInfo getChartInfo() {
        Integer tenantCount = tenantInfoMapper.selectCount(Wrappers.lambdaQuery(TenantInfo.class).eq(TenantInfo::getDelFlag, DelEnum.NORMAL.getIntCode()));
        Integer itemCount = itemInfoMapper.selectCount(Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getDelFlag, DelEnum.NORMAL.getIntCode()));
        Integer threadPoolCount = configInfoMapper.selectCount(Wrappers.lambdaQuery(ConfigAllInfo.class).eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL.getIntCode()));
        ChartInfo chartInfo = new ChartInfo();
        chartInfo.setTenantCount(tenantCount)
                .setItemCount(itemCount)
                .setThreadPoolCount(threadPoolCount)
                .setThreadPoolInstanceCount(ConfigCacheService.getTotal());
        return chartInfo;
    }

    @Override
    public LineChartInfo getLineChatInfo() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = currentDate.plusMinutes(-10);
        long currentTime = DateUtil.getTime(currentDate);
        long startTime = DateUtil.getTime(startDate);
        List<HisRunDataMapper.ThreadPoolTaskRanking> threadPoolTaskRankings = hisRunDataMapper.queryThreadPoolMaxRanking(startTime, currentTime);
        List<Object> oneList = new ArrayList<>();
        List<Object> twoList = new ArrayList<>();
        List<Object> threeList = new ArrayList<>();
        List<Object> fourList = new ArrayList<>();
        ArrayList<List<Object>> lists = CollectionUtil.newArrayList(oneList, twoList, threeList, fourList);
        for (int i = 0; i < threadPoolTaskRankings.size(); i++) {
            List<Object> eachList = lists.get(i);
            HisRunDataMapper.ThreadPoolTaskRanking taskRanking = threadPoolTaskRankings.get(i);
            eachList.add(taskRanking.getTpId());
            eachList.add(taskRanking.getMaxQueueSize());
            eachList.add(taskRanking.getMaxRejectCount());
            eachList.add(taskRanking.getMaxCompletedTaskCount());
        }
        return new LineChartInfo(oneList, twoList, threeList, fourList);
    }

    @Override
    public TenantChart getTenantChart() {
        List<Map<String, Object>> tenantChartList = new ArrayList<>();
        List<TenantInfo> tenantInfos = tenantInfoMapper.selectList(Wrappers.lambdaQuery(TenantInfo.class).eq(TenantInfo::getDelFlag, DelEnum.NORMAL.getIntCode()));
        for (TenantInfo tenant : tenantInfos) {
            int tenantThreadPoolNum = 0;
            LambdaQueryWrapper<ItemInfo> itemQueryWrapper =
                    Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getTenantId, tenant.getTenantId()).eq(ItemInfo::getDelFlag, DelEnum.NORMAL.getIntCode()).select(ItemInfo::getItemId);
            List<ItemInfo> itemInfos = itemInfoMapper.selectList(itemQueryWrapper);
            for (ItemInfo item : itemInfos) {
                LambdaQueryWrapper<ConfigAllInfo> threadPoolQueryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                        .eq(ConfigInfoBase::getItemId, item.getItemId())
                        .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL.getIntCode());
                Integer threadPoolCount = configInfoMapper.selectCount(threadPoolQueryWrapper);
                tenantThreadPoolNum += threadPoolCount;
            }
            Map<String, Object> dict = new LinkedHashMap<>();
            dict.put("name", tenant.getTenantId());
            dict.put("value", tenantThreadPoolNum);
            tenantChartList.add(dict);
        }
        List<Map<String, Object>> resultTenantChartList = tenantChartList.stream()
                .sorted((one, two) -> (int) two.get("value") - (int) one.get("value"))
                .limit(5)
                .collect(Collectors.toList());
        return new TenantChart(resultTenantChartList);
    }

    @Override
    public PieChartInfo getPieChart() {
        LambdaQueryWrapper<ItemInfo> itemQueryWrapper = Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getDelFlag, DelEnum.NORMAL.getIntCode()).select(ItemInfo::getItemId);
        List<Object> itemNameList = itemInfoMapper.selectObjs(itemQueryWrapper);
        List<Map<String, Object>> pieDataList = new ArrayList<>();
        for (Object each : itemNameList) {
            LambdaQueryWrapper<ConfigAllInfo> threadPoolQueryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                    .eq(ConfigInfoBase::getItemId, each)
                    .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL.getIntCode());
            Integer threadPoolCount = configInfoMapper.selectCount(threadPoolQueryWrapper);
            if (threadPoolCount != null) {
                Map<String, Object> dict = new LinkedHashMap<>();
                dict.put("name", each);
                dict.put("value", threadPoolCount);
                pieDataList.add(dict);
            }
        }
        pieDataList.sort((one, two) -> (int) two.get("value") - (int) one.get("value"));
        List<String> resultItemIds = new ArrayList<>();
        List<Map<String, Object>> resultPieDataList = pieDataList.stream()
                .limit(5)
                .map(each -> {
                    resultItemIds.add(each.get("name").toString());
                    return each;
                })
                .collect(Collectors.toList());
        return new PieChartInfo(resultItemIds, resultPieDataList);
    }

    @Override
    public RankingChart getRankingChart() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = currentDate.plusMinutes(-10);
        long currentTime = DateUtil.getTime(currentDate);
        long startTime = DateUtil.getTime(startDate);
        List<RankingChart.RankingChartInfo> resultList = new ArrayList<>();
        List<HisRunDataMapper.ThreadPoolTaskRanking> threadPoolTaskRankings = hisRunDataMapper.queryThreadPoolTaskSumRanking(startTime, currentTime);
        threadPoolTaskRankings.forEach(each -> {
            RankingChart.RankingChartInfo rankingChartInfo = new RankingChart.RankingChartInfo();
            rankingChartInfo.setMaxCompletedTaskCount(each.getMaxCompletedTaskCount());
            List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(each.getItemId());
            Lease<InstanceInfo> first = CollectionUtil.getFirst(leases);
            if (first == null) {
                rankingChartInfo.setInst(0);
            } else {
                InstanceInfo holder = first.getHolder();
                String itemTenantKey = holder.getGroupKey();
                String groupKey = getGroupKey(each.getTpId(), itemTenantKey);
                Map<String, CacheItem> content = ConfigCacheService.getContent(groupKey);
                rankingChartInfo.setInst(content.keySet().size());
            }
            String keyTenant = GroupKey.getKeyTenant(each.getTenantId(), each.getItemId(), each.getTpId());
            rankingChartInfo.setGroupKey(keyTenant);
            resultList.add(rankingChartInfo);
        });
        return new RankingChart(resultList);
    }
}
