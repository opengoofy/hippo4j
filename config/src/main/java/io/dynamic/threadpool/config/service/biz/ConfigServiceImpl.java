package io.dynamic.threadpool.config.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.dynamic.threadpool.common.toolkit.ContentUtil;
import io.dynamic.threadpool.common.toolkit.Md5Util;
import io.dynamic.threadpool.config.event.LocalDataChangeEvent;
import io.dynamic.threadpool.config.mapper.ConfigInfoMapper;
import io.dynamic.threadpool.config.model.ConfigAllInfo;
import io.dynamic.threadpool.config.service.ConfigChangePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 服务端配置接口实现
 *
 * @author chen.ma
 * @date 2021/6/20 15:21
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigInfoMapper configInfoMapper;

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
    public void insertOrUpdate(ConfigAllInfo configAllInfo) {
        try {
            addConfigInfo(configAllInfo);
        } catch (Exception ex) {
            updateConfigInfo(configAllInfo);
        }

        ConfigChangePublisher
                .notifyConfigChange(new LocalDataChangeEvent(ContentUtil.getGroupKey(configAllInfo)));
    }

    private Integer addConfigInfo(ConfigAllInfo config) {
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

    private void updateConfigInfo(ConfigAllInfo config) {
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
