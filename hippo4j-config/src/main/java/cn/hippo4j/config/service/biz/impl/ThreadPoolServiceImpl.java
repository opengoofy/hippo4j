package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.enums.DelEnum;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolDelReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.config.service.biz.ThreadPoolService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hippo4j.tools.logrecord.annotation.LogRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Thread pool service impl.
 *
 * @author chen.ma
 * @date 2021/6/30 21:26
 */
@Service
@AllArgsConstructor
public class ThreadPoolServiceImpl implements ThreadPoolService {

    private final ConfigService configService;

    private final ConfigInfoMapper configInfoMapper;

    @Override
    public IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(!StringUtils.isBlank(reqDTO.getTenantId()), ConfigAllInfo::getTenantId, reqDTO.getTenantId())
                .eq(!StringUtils.isBlank(reqDTO.getItemId()), ConfigAllInfo::getItemId, reqDTO.getItemId())
                .eq(!StringUtils.isBlank(reqDTO.getTpId()), ConfigAllInfo::getTpId, reqDTO.getTpId())
                .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL)
                .orderByDesc(ConfigAllInfo::getGmtCreate);

        return configInfoMapper.selectPage((IPage)reqDTO, wrapper).convert(each -> BeanUtil.convert(each, ThreadPoolRespDTO.class));
    }

    @Override
    public ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO) {
        ConfigAllInfo configAllInfo = configService.findConfigAllInfo(reqDTO.getTpId(), reqDTO.getItemId(), reqDTO.getTenantId());
        return BeanUtil.convert(configAllInfo, ThreadPoolRespDTO.class);
    }

    @Override
    public List<ThreadPoolRespDTO> getThreadPoolByItemId(String itemId) {
        LambdaQueryWrapper<ConfigAllInfo> queryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getItemId, itemId);

        List<ConfigAllInfo> selectList = configInfoMapper.selectList(queryWrapper);
        return BeanUtil.convert(selectList, ThreadPoolRespDTO.class);
    }

    @Override
    public void saveOrUpdateThreadPoolConfig(String identify, ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        configService.insertOrUpdate(identify, BeanUtil.convert(reqDTO, ConfigAllInfo.class));
    }

    @LogRecord(
            bizNo = "{{#reqDTO.itemId}}_{{#reqDTO.tpId}}",
            category = "THREAD_POOL_DELETE",
            success = "删除线程池: {{#reqDTO.tpId}}",
            detail = "{{#reqDTO.toString()}}"
    )
    @Override
    public void deletePool(ThreadPoolDelReqDTO reqDTO) {
        configInfoMapper.delete(
                Wrappers.lambdaUpdate(ConfigAllInfo.class)
                        .eq(ConfigAllInfo::getTenantId, reqDTO.getTenantId())
                        .eq(ConfigAllInfo::getItemId, reqDTO.getItemId())
                        .eq(ConfigAllInfo::getTpId, reqDTO.getTpId())
        );
    }

    @Override
    public void alarmEnable(String id, Integer isAlarm) {
        ConfigAllInfo configAllInfo = configInfoMapper.selectById(id);
        configAllInfo.setIsAlarm(isAlarm);
        // TODO: 是否报警变更, 虽然通知了客户端, 但是并没有在客户端实时生效, 需要考虑一个好的场景思路
        configService.insertOrUpdate(null, configAllInfo);
    }

}
