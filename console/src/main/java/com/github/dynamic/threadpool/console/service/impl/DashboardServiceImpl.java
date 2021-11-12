package com.github.dynamic.threadpool.console.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dynamic.threadpool.config.enums.DelEnum;
import com.github.dynamic.threadpool.config.mapper.ConfigInfoMapper;
import com.github.dynamic.threadpool.config.mapper.ItemInfoMapper;
import com.github.dynamic.threadpool.config.mapper.TenantInfoMapper;
import com.github.dynamic.threadpool.config.model.ConfigAllInfo;
import com.github.dynamic.threadpool.config.model.ItemInfo;
import com.github.dynamic.threadpool.config.model.TenantInfo;
import com.github.dynamic.threadpool.console.model.ChartInfo;
import com.github.dynamic.threadpool.console.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public ChartInfo getChartInfo() {
        Integer tenantCount = tenantInfoMapper.selectCount(Wrappers.lambdaQuery(TenantInfo.class).eq(TenantInfo::getDelFlag, DelEnum.NORMAL));
        Integer itemCount = itemInfoMapper.selectCount(Wrappers.lambdaQuery(ItemInfo.class).eq(ItemInfo::getDelFlag, DelEnum.NORMAL));
        Integer threadPoolCount = configInfoMapper.selectCount(Wrappers.lambdaQuery(ConfigAllInfo.class).eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL));

        ChartInfo chartInfo = new ChartInfo();
        chartInfo.setTenantCount(tenantCount)
                .setItemCount(itemCount)
                .setThreadPoolCount(threadPoolCount);
        return chartInfo;
    }

}
