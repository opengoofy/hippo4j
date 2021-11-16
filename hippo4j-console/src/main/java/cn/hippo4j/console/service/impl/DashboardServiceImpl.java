package cn.hippo4j.console.service.impl;

import cn.hippo4j.console.model.ChartInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.hippo4j.config.enums.DelEnum;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.mapper.ItemInfoMapper;
import cn.hippo4j.config.mapper.TenantInfoMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ItemInfo;
import cn.hippo4j.config.model.TenantInfo;
import cn.hippo4j.console.service.DashboardService;
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
