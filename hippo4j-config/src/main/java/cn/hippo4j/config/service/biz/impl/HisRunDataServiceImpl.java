package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.config.mapper.HisRunDataMapper;
import cn.hippo4j.config.model.HisRunDataInfo;
import cn.hippo4j.config.model.biz.monitor.MonitorActiveRespDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorQueryReqDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorRespDTO;
import cn.hippo4j.config.service.biz.HisRunDataService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static cn.hutool.core.date.DatePattern.NORM_TIME_PATTERN;

/**
 * His run data service impl.
 *
 * @author chen.ma
 * @date 2021/12/10 21:28
 */
@Service
public class HisRunDataServiceImpl extends ServiceImpl<HisRunDataMapper, HisRunDataInfo> implements HisRunDataService {

    @Value("${clean.history.data.period:30}")
    private Long cleanHistoryDataPeriod;

    @Override
    public List<MonitorRespDTO> query(MonitorQueryReqDTO reqDTO) {
        Date currentDate = new Date();
        DateTime dateTime = DateUtil.offsetMinute(currentDate, (int) -cleanHistoryDataPeriod);
        long startTime = dateTime.getTime();

        List<HisRunDataInfo> hisRunDataInfos = this.lambdaQuery()
                .eq(HisRunDataInfo::getTenantId, reqDTO.getTenantId())
                .eq(HisRunDataInfo::getItemId, reqDTO.getItemId())
                .eq(HisRunDataInfo::getTpId, reqDTO.getTpId())
                .eq(HisRunDataInfo::getInstanceId, reqDTO.getInstanceId())
                .between(HisRunDataInfo::getTimestamp, startTime, currentDate.getTime())
                .orderByAsc(HisRunDataInfo::getTimestamp)
                .list();

        return BeanUtil.convert(hisRunDataInfos, MonitorRespDTO.class);
    }

    @Override
    public MonitorActiveRespDTO queryInfoThreadPoolMonitor(MonitorQueryReqDTO reqDTO) {
        Date currentDate = new Date();
        DateTime dateTime = DateUtil.offsetMinute(currentDate, (int) -cleanHistoryDataPeriod);
        long startTime = dateTime.getTime();

        List<HisRunDataInfo> hisRunDataInfos = this.lambdaQuery()
                .eq(HisRunDataInfo::getTenantId, reqDTO.getTenantId())
                .eq(HisRunDataInfo::getItemId, reqDTO.getItemId())
                .eq(HisRunDataInfo::getTpId, reqDTO.getTpId())
                .eq(HisRunDataInfo::getInstanceId, reqDTO.getInstanceId())
                .between(HisRunDataInfo::getTimestamp, startTime, currentDate.getTime())
                .orderByAsc(HisRunDataInfo::getTimestamp)
                .list();

        List<String> times = Lists.newArrayList();
        List<Long> poolSizeList = Lists.newArrayList();
        List<Long> activeSizeList = Lists.newArrayList();
        List<Long> queueSizeList = Lists.newArrayList();
        List<Long> completedTaskCountList = Lists.newArrayList();
        List<Long> rejectCountList = Lists.newArrayList();
        List<Long> queueRemainingCapacityList = Lists.newArrayList();
        List<Long> currentLoadList = Lists.newArrayList();

        hisRunDataInfos.forEach(each -> {
            String time = DateUtil.format(new Date(each.getTimestamp()), NORM_TIME_PATTERN);
            times.add(time);
            poolSizeList.add(each.getPoolSize());
            activeSizeList.add(each.getActiveSize());
            queueSizeList.add(each.getQueueSize());
            completedTaskCountList.add(each.getCompletedTaskCount());
            rejectCountList.add(each.getRejectCount());
            queueRemainingCapacityList.add(each.getQueueRemainingCapacity());
            currentLoadList.add(each.getCurrentLoad());
        });

        return new MonitorActiveRespDTO(times, poolSizeList, activeSizeList, queueSizeList, completedTaskCountList, rejectCountList, queueRemainingCapacityList, currentLoadList);
    }

    @Override
    public void save(Message message) {
        List<RuntimeMessage> runtimeMessages = message.getMessages();
        List<HisRunDataInfo> hisRunDataInfos = Lists.newArrayList();

        runtimeMessages.forEach(each -> {
            HisRunDataInfo hisRunDataInfo = BeanUtil.convert(each, HisRunDataInfo.class);
            String[] parseKey = GroupKey.parseKey(each.getGroupKey());

            hisRunDataInfo.setTpId(parseKey[0]);
            hisRunDataInfo.setItemId(parseKey[1]);
            hisRunDataInfo.setTenantId(parseKey[2]);
            hisRunDataInfo.setInstanceId(parseKey[3]);

            hisRunDataInfos.add(hisRunDataInfo);
        });

        this.saveBatch(hisRunDataInfos);
    }

}
