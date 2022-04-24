package cn.hippo4j.config.monitor;

import cn.hippo4j.common.executor.ExecutorFactory;
import cn.hippo4j.config.config.ServerBootstrapProperties;
import cn.hippo4j.config.model.HisRunDataInfo;
import cn.hippo4j.config.service.biz.HisRunDataService;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.DEFAULT_GROUP;

/**
 * Regularly clean up the historical running data of thread pool.
 *
 * @author chen.ma
 * @date 2021/12/17 20:13
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
        Date currentDate = new Date();
        DateTime offsetMinuteDateTime = DateUtil.offsetMinute(currentDate, -properties.getCleanHistoryDataPeriod());

        LambdaQueryWrapper<HisRunDataInfo> queryWrapper = Wrappers.lambdaQuery(HisRunDataInfo.class)
                .le(HisRunDataInfo::getTimestamp, offsetMinuteDateTime.getTime());

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
