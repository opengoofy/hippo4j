package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.enums.DelEnum;
import cn.hippo4j.common.toolkit.*;
import cn.hippo4j.common.web.exception.ServiceException;
import cn.hippo4j.config.event.LocalDataChangeEvent;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.mapper.ConfigInstanceMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ConfigInfoBase;
import cn.hippo4j.config.model.ConfigInstanceInfo;
import cn.hippo4j.config.service.ConfigChangePublisher;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hippo4j.tools.logrecord.annotation.LogRecord;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hippo4j.config.service.ConfigCacheService.getContent;

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

    private final ConfigInstanceMapper configInstanceMapper;

    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenantId) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(StrUtil.isNotBlank(tpId), ConfigAllInfo::getTpId, tpId)
                .eq(StrUtil.isNotBlank(itemId), ConfigAllInfo::getItemId, itemId)
                .eq(StrUtil.isNotBlank(tenantId), ConfigAllInfo::getTenantId, tenantId);

        ConfigAllInfo configAllInfo = configInfoMapper.selectOne(wrapper);
        return configAllInfo;
    }

    @Override
    public ConfigAllInfo findConfigRecentInfo(String... params) {
        ConfigAllInfo resultConfig;
        ConfigAllInfo configInstance = null;

        String instanceId = params[3];
        if (StrUtil.isNotBlank(instanceId)) {
            LambdaQueryWrapper<ConfigInstanceInfo> instanceQueryWrapper = Wrappers.lambdaQuery(ConfigInstanceInfo.class)
                    .eq(ConfigInstanceInfo::getTpId, params[0])
                    .eq(ConfigInstanceInfo::getItemId, params[1])
                    .eq(ConfigInstanceInfo::getTenantId, params[2])
                    .eq(ConfigInstanceInfo::getInstanceId, params[3])
                    .orderByDesc(ConfigInstanceInfo::getGmtCreate)
                    .last("LIMIT 1");

            ConfigInstanceInfo instanceInfo = configInstanceMapper.selectOne(instanceQueryWrapper);
            if (instanceInfo != null) {
                String content = instanceInfo.getContent();
                configInstance = JSONUtil.parseObject(content, ConfigAllInfo.class);
                configInstance.setContent(content);
                configInstance.setGmtCreate(instanceInfo.getGmtCreate());
                configInstance.setMd5(Md5Util.getTpContentMd5(configInstance));
            }
        }

        ConfigAllInfo configAllInfo = findConfigAllInfo(params[0], params[1], params[2]);
        if (configAllInfo == null && configInstance == null) {
            throw new ServiceException("Thread pool configuration is not defined.");
        } else if (configAllInfo != null && configInstance == null) {
            resultConfig = configAllInfo;
        } else if (configAllInfo == null && configInstance != null) {
            resultConfig = configInstance;
        } else {
            if (configAllInfo.getGmtModified().before(configInstance.getGmtCreate())) {
                resultConfig = configInstance;
            } else {
                resultConfig = configAllInfo;
            }
        }

        return resultConfig;
    }

    @Override
    public void insertOrUpdate(String identify, ConfigAllInfo configInfo) {
        verification(identify);
        LambdaQueryWrapper<ConfigAllInfo> queryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getTenantId, configInfo.getTenantId())
                .eq(ConfigInfoBase::getItemId, configInfo.getItemId())
                .eq(ConfigInfoBase::getTpId, configInfo.getTpId());
        ConfigAllInfo existConfig = configInfoMapper.selectOne(queryWrapper);

        ConfigServiceImpl configService = ApplicationContextHolder.getBean(this.getClass());
        configInfo.setCapacity(getQueueCapacityByType(configInfo));

        ConditionUtil
                .condition(
                        existConfig == null,
                        () -> configService.addConfigInfo(configInfo),
                        () -> configService.updateConfigInfo(identify, configInfo)
                );

        ConfigChangePublisher.notifyConfigChange(new LocalDataChangeEvent(identify, ContentUtil.getGroupKey(configInfo)));
    }

    private void verification(String identify) {
        if (StringUtil.isNotBlank(identify)) {
            Map content = getContent(identify);
            Assert.isTrue(CollectionUtil.isNotEmpty(content), "线程池实例不存在, 请尝试页面刷新.");
        }
    }

    public Long addConfigInfo(ConfigAllInfo config) {
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5Util.getTpContentMd5(config));

        try {
            // 当前为单体应用, 后续支持集群部署时切换分布式锁.
            synchronized (ConfigService.class) {
                ConfigAllInfo configAllInfo = configInfoMapper.selectOne(
                        Wrappers.lambdaQuery(ConfigAllInfo.class)
                                .eq(ConfigAllInfo::getTpId, config.getTpId())
                                .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL.getIntCode())
                );
                Assert.isNull(configAllInfo, "线程池配置已存在.");

                if (SqlHelper.retBool(configInfoMapper.insert(config))) {
                    return config.getId();
                }
            }
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }

        return null;
    }

    @LogRecord(
            bizNo = "{{#config.itemId}}_{{#config.tpId}}",
            category = "THREAD_POOL_UPDATE",
            success = "核心线程: {{#config.coreSize}}, 最大线程: {{#config.maxSize}}, 队列类型: {{#config.queueType}}, 队列容量: {{#config.capacity}}, 拒绝策略: {{#config.rejectedType}}",
            detail = "{{#config.toString()}}"
    )
    public void updateConfigInfo(String identify, ConfigAllInfo config) {
        LambdaUpdateWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaUpdate(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getTpId, config.getTpId())
                .eq(ConfigAllInfo::getItemId, config.getItemId())
                .eq(ConfigAllInfo::getTenantId, config.getTenantId());

        config.setGmtCreate(null);
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5Util.getTpContentMd5(config));

        try {
            // 创建线程池配置实例临时配置, 也可以当作历史配置, 不过针对的是单节点
            if (StrUtil.isNotBlank(identify)) {
                ConfigInstanceInfo instanceInfo = BeanUtil.convert(config, ConfigInstanceInfo.class);
                instanceInfo.setInstanceId(identify);
                configInstanceMapper.insert(instanceInfo);
                return;
            }

            configInfoMapper.update(config, wrapper);
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 根据队列类型获取队列大小.
     * <p>
     * 不支持设置队列大小 {@link SynchronousQueue} {@link LinkedTransferQueue}
     *
     * @param config
     * @return
     */
    private Integer getQueueCapacityByType(ConfigAllInfo config) {
        int queueCapacity;
        switch (config.getQueueType()) {
            case 5:
                queueCapacity = Integer.MAX_VALUE;
                break;
            default:
                queueCapacity = config.getCapacity();
                break;
        }

        List<Integer> queueTypes = Stream.of(1, 2, 3, 6, 9).collect(Collectors.toList());
        boolean setDefaultFlag = queueTypes.contains(config.getQueueType()) && (config.getCapacity() == null || Objects.equals(config.getCapacity(), 0));
        if (setDefaultFlag) {
            queueCapacity = 1024;
        }

        return queueCapacity;
    }

}
