package cn.hippo4j.console.service.impl;

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.enums.DelEnum;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.mapper.HisRunDataMapper;
import cn.hippo4j.config.mapper.ItemInfoMapper;
import cn.hippo4j.config.mapper.TenantInfoMapper;
import cn.hippo4j.config.model.*;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.service.biz.HisRunDataService;
import cn.hippo4j.console.model.*;
import cn.hippo4j.console.service.DashboardService;
import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hippo4j.common.toolkit.ContentUtil.getGroupKey;

/**
 * Dashboard service impl.
 *
 * @author chen.ma
 * @date 2021/11/10 21:08
 */
@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TenantInfoMapper tenantInfoMapper;

    private final ItemInfoMapper itemInfoMapper;

    private final ConfigInfoMapper configInfoMapper;

    private final HisRunDataService hisRunDataService;

    private final HisRunDataMapper hisRunDataMapper;

    private final BaseInstanceRegistry baseInstanceRegistry;

    @Override
    public ChartInfo getChartInfo() {
        Integer tenantCount = tenantInfoMapper.selectCount(Wrappers.lambdaQuery(TenantInfo.class).eq(TenantInfo::getDelFlag, DelEnum.NORMAL));
        Integer itemCount = itemInfoMapper.selectCount(Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getDelFlag, DelEnum.NORMAL));
        Integer threadPoolCount = configInfoMapper.selectCount(Wrappers.lambdaQuery(ConfigAllInfo.class).eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL));

        ChartInfo chartInfo = new ChartInfo();
        chartInfo.setTenantCount(tenantCount)
                .setItemCount(itemCount)
                .setThreadPoolCount(threadPoolCount)
                .setThreadPoolInstanceCount(ConfigCacheService.getTotal());
        return chartInfo;
    }

    @Override
    public LineChartInfo getLineChatInfo() {
        Date currentDate = new Date();
        DateTime startTime = DateUtil.offsetMinute(currentDate, -10);

        List<HisRunDataMapper.ThreadPoolTaskRanking> threadPoolTaskRankings = hisRunDataMapper.queryThreadPoolMaxRanking(startTime.getTime(), currentDate.getTime());

        List<Object> oneList = Lists.newArrayList();
        List<Object> twoList = Lists.newArrayList();
        List<Object> threeList = Lists.newArrayList();
        List<Object> fourList = Lists.newArrayList();

        ArrayList<List<Object>> lists = Lists.newArrayList(oneList, twoList, threeList, fourList);
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
        List<Map<String, Object>> tenantChartList = Lists.newArrayList();
        List<TenantInfo> tenantInfos = tenantInfoMapper.selectList(Wrappers.lambdaQuery(TenantInfo.class).eq(TenantInfo::getDelFlag, DelEnum.NORMAL));
        for (TenantInfo tenant : tenantInfos) {
            int tenantThreadPoolNum = 0;
            LambdaQueryWrapper<ItemInfo> itemQueryWrapper = Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getTenantId, tenant.getTenantId()).eq(ItemInfo::getDelFlag, DelEnum.NORMAL).select(ItemInfo::getItemId);
            List<ItemInfo> itemInfos = itemInfoMapper.selectList(itemQueryWrapper);
            for (ItemInfo item : itemInfos) {
                LambdaQueryWrapper<ConfigAllInfo> threadPoolQueryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                        .eq(ConfigInfoBase::getItemId, item.getItemId())
                        .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL);
                Integer threadPoolCount = configInfoMapper.selectCount(threadPoolQueryWrapper);
                tenantThreadPoolNum += threadPoolCount;
            }

            Dict dict = Dict.create().set("name", tenant.getTenantId()).set("value", tenantThreadPoolNum);
            tenantChartList.add(dict);
        }

        List resultTenantChartList = tenantChartList.stream()
                .sorted((one, two) -> (int) two.get("value") - (int) one.get("value"))
                .limit(5)
                .collect(Collectors.toList());

        return new TenantChart(resultTenantChartList);
    }

    @Override
    public PieChartInfo getPieChart() {
        LambdaQueryWrapper<ItemInfo> itemQueryWrapper = Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getDelFlag, DelEnum.NORMAL).select(ItemInfo::getItemId);
        List<Object> itemNameList = itemInfoMapper.selectObjs(itemQueryWrapper);

        List<Map<String, Object>> pieDataList = Lists.newArrayList();
        for (Object each : itemNameList) {
            LambdaQueryWrapper<ConfigAllInfo> threadPoolQueryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                    .eq(ConfigInfoBase::getItemId, each)
                    .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL);
            Integer threadPoolCount = configInfoMapper.selectCount(threadPoolQueryWrapper);
            if (threadPoolCount != null) {
                Dict dict = Dict.create().set("name", each).set("value", threadPoolCount);
                pieDataList.add(dict);
            }
        }

        pieDataList.sort((one, two) -> (int) two.get("value") - (int) one.get("value"));

        List<String> resultItemIds = Lists.newArrayList();
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
        Date currentDate = new Date();
        DateTime tenTime = DateUtil.offsetMinute(currentDate, -10);

        List<RankingChart.RankingChartInfo> resultList = Lists.newArrayList();
        List<HisRunDataMapper.ThreadPoolTaskRanking> threadPoolTaskRankings = hisRunDataMapper.queryThreadPoolTaskSumRanking(tenTime.getTime(), currentDate.getTime());
        threadPoolTaskRankings.forEach(each -> {
            RankingChart.RankingChartInfo rankingChartInfo = new RankingChart.RankingChartInfo();
            rankingChartInfo.setMaxCompletedTaskCount(each.getMaxCompletedTaskCount());
            List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(each.getItemId());
            Lease<InstanceInfo> first = CollUtil.getFirst(leases);
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
