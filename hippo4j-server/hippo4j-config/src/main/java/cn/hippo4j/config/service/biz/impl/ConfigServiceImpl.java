/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.enums.DelEnum;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterServerNotifyParameter;
import cn.hippo4j.common.toolkit.*;
import cn.hippo4j.common.web.exception.ServiceException;
import cn.hippo4j.config.event.LocalDataChangeEvent;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.mapper.ConfigInstanceMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.ConfigInfoBase;
import cn.hippo4j.config.model.ConfigInstanceInfo;
import cn.hippo4j.config.model.LogRecordInfo;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hippo4j.config.service.ConfigChangePublisher;
import cn.hippo4j.config.service.biz.*;
import cn.hippo4j.common.toolkit.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hippo4j.config.service.ConfigCacheService.getContent;

/**
 * Config service impl.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigInfoMapper configInfoMapper;

    private final ConfigInstanceMapper configInstanceMapper;

    private final OperationLogService operationLogService;

    private final NotifyService notifyService;

    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenantId) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(StringUtil.isNotBlank(tpId), ConfigAllInfo::getTpId, tpId)
                .eq(StringUtil.isNotBlank(itemId), ConfigAllInfo::getItemId, itemId)
                .eq(StringUtil.isNotBlank(tenantId), ConfigAllInfo::getTenantId, tenantId);
        ConfigAllInfo configAllInfo = configInfoMapper.selectOne(wrapper);
        return configAllInfo;
    }

    @Override
    public ConfigAllInfo findConfigRecentInfo(String... params) {
        ConfigAllInfo resultConfig;
        ConfigAllInfo configInstance = null;
        String instanceId = params[3];
        if (StringUtil.isNotBlank(instanceId)) {
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
            throw new ServiceException("Thread pool configuration is not defined");
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
    public void insertOrUpdate(String identify, boolean isChangeNotice, ConfigAllInfo configInfo) {
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
                        () -> configService.updateConfigInfo(identify, isChangeNotice, configInfo));
        if (isChangeNotice) {
            ConfigChangePublisher.notifyConfigChange(new LocalDataChangeEvent(identify, ContentUtil.getGroupKey(configInfo)));
        }
    }

    @Override
    public void register(DynamicThreadPoolRegisterWrapper registerWrapper) {
        ConfigAllInfo configAllInfo = parseConfigAllInfo(registerWrapper);
        TenantService tenantService = ApplicationContextHolder.getBean(TenantService.class);
        ItemService itemService = ApplicationContextHolder.getBean(ItemService.class);
        Assert.isTrue(tenantService.getTenantByTenantId(registerWrapper.getTenantId()) != null, "Tenant does not exist");
        Assert.isTrue(itemService.queryItemById(registerWrapper.getTenantId(), registerWrapper.getItemId()) != null, "Item does not exist");
        ConfigAllInfo existConfigAllInfo = findConfigAllInfo(configAllInfo.getTpId(), registerWrapper.getItemId(), registerWrapper.getTenantId());
        if (existConfigAllInfo == null) {
            addConfigInfo(configAllInfo);
        } else if (registerWrapper.getUpdateIfExists()) {
            ConfigServiceImpl configService = ApplicationContextHolder.getBean(this.getClass());
            configService.updateConfigInfo(null, false, configAllInfo);
        }
        DynamicThreadPoolRegisterServerNotifyParameter serverNotifyParameter = registerWrapper.getServerNotify();
        if (serverNotifyParameter != null) {
            ArrayList<String> notifyTypes = new ArrayList<>();
            Collections.addAll(notifyTypes, "CONFIG", "ALARM");
            notifyTypes.forEach(each -> {
                NotifyReqDTO notifyReqDTO = new NotifyReqDTO();
                notifyReqDTO.setType(each)
                        .setEnable(1)
                        .setTenantId(registerWrapper.getTenantId())
                        .setItemId(registerWrapper.getItemId())
                        .setTpId(configAllInfo.getTpId())
                        .setPlatform(serverNotifyParameter.getPlatform())
                        .setReceives(serverNotifyParameter.getReceives())
                        .setSecretKey(serverNotifyParameter.getAccessToken());
                if (Objects.equals(each, "ALARM")) {
                    notifyReqDTO.setInterval(serverNotifyParameter.getInterval());
                    notifyReqDTO.setAlarmType(true);
                } else {
                    notifyReqDTO.setConfigType(true);
                }
                notifyService.saveOrUpdate(registerWrapper.getNotifyUpdateIfExists(), notifyReqDTO);
            });
        }
    }

    private ConfigAllInfo parseConfigAllInfo(DynamicThreadPoolRegisterWrapper registerWrapper) {
        DynamicThreadPoolRegisterParameter registerParameter = registerWrapper.getParameter();
        ConfigAllInfo configAllInfo = JSONUtil.parseObject(JSONUtil.toJSONString(registerParameter), ConfigAllInfo.class);
        configAllInfo.setTenantId(registerWrapper.getTenantId());
        configAllInfo.setItemId(registerWrapper.getItemId());
        configAllInfo.setTpId(registerParameter.getThreadPoolId());
        configAllInfo.setLivenessAlarm(registerParameter.getActiveAlarm());
        configAllInfo.setQueueType(registerParameter.getBlockingQueueType().getType());
        configAllInfo.setRejectedType(registerParameter.getRejectedPolicyType().getType());
        configAllInfo.setAllowCoreThreadTimeOut(registerParameter.getAllowCoreThreadTimeOut());
        return configAllInfo;
    }

    private void verification(String identify) {
        if (StringUtil.isNotBlank(identify)) {
            Map content = getContent(identify);
            Assert.isTrue(CollectionUtil.isNotEmpty(content), "线程池实例不存在, 请尝试页面刷新");
        }
    }

    public Long addConfigInfo(ConfigAllInfo config) {
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5Util.getTpContentMd5(config));
        try {
            // Currently it is a single application, and it supports switching distributed locks during cluster deployment in the future.
            synchronized (ConfigService.class) {
                ConfigAllInfo configAllInfo = configInfoMapper.selectOne(
                        Wrappers.lambdaQuery(ConfigAllInfo.class)
                                .eq(ConfigAllInfo::getTpId, config.getTpId())
                                .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL.getIntCode()));
                Assert.isNull(configAllInfo, "线程池配置已存在");
                if (SqlHelper.retBool(configInfoMapper.insert(config))) {
                    return config.getId();
                }
            }
        } catch (Exception ex) {
            log.error("[db-error] message: {}", ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public void updateConfigInfo(String identify, boolean isChangeNotice, ConfigAllInfo config) {
        LambdaUpdateWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaUpdate(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getTpId, config.getTpId())
                .eq(ConfigAllInfo::getItemId, config.getItemId())
                .eq(ConfigAllInfo::getTenantId, config.getTenantId());
        config.setGmtCreate(null);
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5Util.getTpContentMd5(config));
        recordOperationLog(config);
        try {
            // Create a temporary configuration of a thread pool configuration instance,
            // which can also be used as a historical configuration, but it is aimed at a single node.
            if (StringUtil.isNotBlank(identify)) {
                ConfigInstanceInfo instanceInfo = BeanUtil.convert(config, ConfigInstanceInfo.class);
                instanceInfo.setInstanceId(identify);
                configInstanceMapper.insert(instanceInfo);
                return;
            } else if (StringUtil.isEmpty(identify) && isChangeNotice) {
                List<String> identifyList = ConfigCacheService.getIdentifyList(config.getTenantId(), config.getItemId(), config.getTpId());
                if (CollectionUtil.isNotEmpty(identifyList)) {
                    for (String each : identifyList) {
                        ConfigInstanceInfo instanceInfo = BeanUtil.convert(config, ConfigInstanceInfo.class);
                        instanceInfo.setInstanceId(each);
                        configInstanceMapper.insert(instanceInfo);
                    }
                }
                return;
            }
            configInfoMapper.update(config, wrapper);
        } catch (Exception ex) {
            log.error("[db-error] message: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private void recordOperationLog(ConfigAllInfo requestParam) {
        LogRecordInfo logRecordInfo = LogRecordInfo.builder()
                .bizKey(requestParam.getItemId() + "_" + requestParam.getTpId())
                .bizNo(requestParam.getItemId() + "_" + requestParam.getTpId())
                .operator(Optional.ofNullable(UserContext.getUserName()).orElse("-"))
                .action(String.format("核心线程: %d, 最大线程: %d, 队列类型: %d, 队列容量: %d, 拒绝策略: %d", requestParam.getCoreSize(), requestParam.getMaxSize(), requestParam.getQueueType(),
                        requestParam.getCapacity(), requestParam.getRejectedType()))
                .category("THREAD_POOL_UPDATE")
                .detail(JSONUtil.toJSONString(requestParam))
                .createTime(new Date())
                .build();
        operationLogService.record(logRecordInfo);
    }

    /**
     * Get queue size based on queue type.
     *
     * <p> 不支持设置队列大小 {@link SynchronousQueue} {@link LinkedTransferQueue}
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
