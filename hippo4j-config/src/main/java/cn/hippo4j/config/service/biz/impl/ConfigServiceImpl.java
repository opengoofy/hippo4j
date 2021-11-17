package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.ConditionUtil;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.config.event.LocalDataChangeEvent;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ConfigInfoBase;
import cn.hippo4j.config.service.ConfigChangePublisher;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.tools.logrecord.annotation.LogRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Config service impl.
 *
 * @author chen.ma
 * @date 2021/6/20 15:21
 */
@Slf4j
@Service
@AllArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigInfoMapper configInfoMapper;

    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenantId) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(!StringUtils.isBlank(tpId), ConfigAllInfo::getTpId, tpId)
                .eq(!StringUtils.isBlank(itemId), ConfigAllInfo::getItemId, itemId)
                .eq(!StringUtils.isBlank(tenantId), ConfigAllInfo::getTenantId, tenantId);

        ConfigAllInfo configAllInfo = configInfoMapper.selectOne(wrapper);
        return configAllInfo;
    }

    @Override
    public void insertOrUpdate(String identify, ConfigAllInfo configInfo) {
        LambdaQueryWrapper<ConfigAllInfo> queryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getTenantId, configInfo.getTenantId())
                .eq(ConfigInfoBase::getItemId, configInfo.getItemId())
                .eq(ConfigInfoBase::getTpId, configInfo.getTpId());
        ConfigAllInfo existConfig = configInfoMapper.selectOne(queryWrapper);

        String userName = UserContext.getUserName();
        ConfigServiceImpl configService = ApplicationContextHolder.getBean(this.getClass());

        try {
            ConditionUtil
                    .condition(
                            existConfig == null,
                            () -> configService.addConfigInfo(configInfo),
                            () -> configService.updateConfigInfo(userName, configInfo)
                    );
        } catch (Exception ex) {
            updateConfigInfo(userName, configInfo);
        }

        ConfigChangePublisher.notifyConfigChange(new LocalDataChangeEvent(identify, ContentUtil.getGroupKey(configInfo)));
    }

    public Integer addConfigInfo(ConfigAllInfo config) {
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5Util.getTpContentMd5(config));

        try {
            if (SqlHelper.retBool(configInfoMapper.insert(config))) {
                return config.getId();
            }
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }

        return null;
    }

    @LogRecord(
            bizNo = "{{#config.itemId}}:{{#config.tpId}}",
            category = "THREAD_POOL_UPDATE",
            operator = "{{#operator}}",
            success = "核心线程: {{#config.coreSize}}, 最大线程: {{#config.maxSize}}, 队列类型: {{#config.queueType}}, 队列容量: {{#config.capacity}}, 拒绝策略: {{#config.rejectedType}}",
            detail = "{{#config.toString()}}"
    )
    public void updateConfigInfo(String operator, ConfigAllInfo config) {
        LambdaUpdateWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaUpdate(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getTpId, config.getTpId())
                .eq(ConfigAllInfo::getItemId, config.getItemId())
                .eq(ConfigAllInfo::getTenantId, config.getTenantId());

        config.setGmtCreate(null);
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5Util.getTpContentMd5(config));

        try {
            configInfoMapper.update(config, wrapper);
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

}
