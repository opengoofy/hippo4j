package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.config.mapper.AlarmInfoMapper;
import cn.hippo4j.config.model.AlarmInfo;
import cn.hippo4j.config.model.biz.alarm.AlarmListRespDTO;
import cn.hippo4j.config.model.biz.alarm.AlarmQueryReqDTO;
import cn.hippo4j.config.service.biz.AlarmService;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报警管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:02
 */
@Service
@AllArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final AlarmInfoMapper alarmInfoMapper;

    @Override
    public List<AlarmListRespDTO> listAlarmConfig(AlarmQueryReqDTO reqDTO) {
        List<AlarmListRespDTO> alarmListRespList = Lists.newArrayList();
        reqDTO.getGroupKeys().forEach(each -> {
            String[] parseKey = GroupKey.parseKey(each);
            LambdaQueryWrapper<AlarmInfo> queryWrapper = Wrappers.lambdaQuery(AlarmInfo.class)
                    .eq(AlarmInfo::getTenantId, parseKey[2])
                    .eq(AlarmInfo::getItemId, parseKey[1])
                    .eq(AlarmInfo::getTpId, parseKey[0]);
            List<AlarmInfo> alarmInfos = alarmInfoMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(alarmInfos)) {
                alarmListRespList.add(new AlarmListRespDTO(parseKey[0], alarmInfos));
            }
        });

        return alarmListRespList;
    }

}
